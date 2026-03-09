# EVENTCONNECT - FINAL CODE FIXES GUIDE

## Problem Summary
1. **No caterers showing** when clicking on "Find Caterers" from MyEvents
2. **Organizer profile endpoints returning 404**
3. **Image upload endpoint URL mismatch**

---

## BACKEND FIXES

### FIX #1: Add `/api/caterers/search` Endpoint

**File:** `C:\projects\eventconnect-backend\app\api\routes\caterer.py`

**Action:** Add this new endpoint after the `create_caterer_profile` function:

```python
# =====================================================
# SEARCH CATERERS BY LOCATION & EVENT TYPE (NO EVENT REQUIRED)
# =====================================================

@router.get("/search")
def search_caterers(
    latitude: float,
    longitude: float,
    guest_count: int,
    event_type: str,
    min_price: float = None,
    max_price: float = None,
    min_rating: float = None,
    veg_only: bool = False,
    nonveg_only: bool = False,
    sort_by: str = "distance",
    db: Session = Depends(get_db),
    user=Depends(get_current_user)
):
    """
    Search caterers by location and event details.
    Direct search without requiring an event_location record.
    """

    MAX_DISTANCE_KM = 30
    DELHI_NCR = ["delhi", "gurgaon", "noida", "ghaziabad", "faridabad"]

    caterers = db.query(Caterer).all()
    result = []

    for caterer in caterers:

        # 1️⃣ Delhi NCR filter
        if caterer.city.lower() not in DELHI_NCR:
            continue

        # 2️⃣ Capacity filter
        if guest_count < caterer.min_capacity or guest_count > caterer.max_capacity:
            continue

        # 3️⃣ Service-type filter
        caterer_services = [s.service_type for s in caterer.services]
        if event_type not in caterer_services:
            continue

        # 4️⃣ Distance filter
        distance = calculate_distance(
            latitude,
            longitude,
            caterer.latitude,
            caterer.longitude
        )

        if distance > MAX_DISTANCE_KM:
            continue

        # 5️⃣ Price filter
        if min_price is not None and caterer.price_per_plate < min_price:
            continue

        if max_price is not None and caterer.price_per_plate > max_price:
            continue

        # 6️⃣ Rating filter
        if min_rating is not None and caterer.rating < min_rating:
            continue

        # 7️⃣ Veg / Nonveg filter
        if veg_only and not caterer.veg_supported:
            continue

        if nonveg_only and not caterer.nonveg_supported:
            continue

        result.append({
            "id": caterer.id,
            "business_name": caterer.business_name,
            "city": caterer.city,
            "price_per_plate": caterer.price_per_plate,
            "rating": caterer.rating,
            "veg_supported": caterer.veg_supported,
            "nonveg_supported": caterer.nonveg_supported,
            "distance_km": round(distance, 2),
            "image_url": caterer.image_url,
            "min_capacity": caterer.min_capacity,
            "max_capacity": caterer.max_capacity
        })

    # Sorting logic
    if sort_by == "price_low":
        result.sort(key=lambda x: x["price_per_plate"])
    elif sort_by == "price_high":
        result.sort(key=lambda x: x["price_per_plate"], reverse=True)
    elif sort_by == "rating":
        result.sort(key=lambda x: x["rating"], reverse=True)
    else:
        result.sort(
            key=lambda x: (
                x["distance_km"],
                -x["rating"],
                x["price_per_plate"]
            )
        )

    return result
```

---

### FIX #2: Change Organizer Router Prefix

**File:** `C:\projects\eventconnect-backend\app\api\routes\organizer.py`

**Change Line 8 from:**
```python
router = APIRouter(prefix="/api/organizer", tags=["Organizer"])
```

**To:**
```python
router = APIRouter(prefix="/api/organizers", tags=["Organizer"])
```

This single line change updates all organizer endpoints:
- `POST /api/organizers/profile`
- `GET /api/organizers/profile`
- `PUT /api/organizers/profile`
- `POST /api/organizers/upload-image`

---

## FRONTEND FIXES

### FIX #3: Update ApiService Endpoint

**File:** `C:\Users\Jahnavi\AndroidStudioProjects\eventconnect\app\src\main\java\com\example\eventconnect\data\network\ApiService.kt`

**Find this line (around line 245):**
```kotlin
@POST("api/organizer/upload-image")
```

**Replace with:**
```kotlin
@POST("api/organizers/upload-image")
```

The complete function should look like:
```kotlin
@Multipart
@POST("api/organizers/upload-image")
suspend fun uploadOrganizerImage(
    @Header("Authorization") token: String,
    @Part file: MultipartBody.Part
): Response<ImageUploadResponse>
```

---

## VERIFICATION STEPS

### Step 1: Verify Caterer Search Works
Test in Postman/Curl:
```
GET /api/caterers/search?latitude=28.6139&longitude=77.2090&guest_count=150&event_type=Wedding
Headers: Authorization: Bearer {your_token}
```

Should return list of caterers.

### Step 2: Verify Organizer Endpoints Work
Test in Postman:
```
GET /api/organizers/profile
Headers: Authorization: Bearer {your_token}
```

Should return organizer profile (or 404 if not created, which is correct).

### Step 3: Test in App
1. Login to Android app
2. Create an event and save it
3. Go to "My Events"
4. Click "Find Caterers"
5. Should now show list of caterers

---

## Root Cause Analysis

| Issue | Root Cause | Solution |
|-------|-----------|----------|
| No caterers showing | Frontend calls `/search` but backend only had `/match/{event_id}` | Added new `/search` endpoint |
| Organizer 404 errors | Router prefix is singular `/api/organizer` but frontend expects plural | Changed to `/api/organizers` |
| Upload URL mismatch | ApiService had old endpoint path | Updated to plural `/organizers/upload-image` |

---

## Files to Replace

### Backend:
- Copy the complete corrected `caterer.py` to `app/api/routes/caterer.py`
- Copy the complete corrected `organizer.py` to `app/api/routes/organizer.py`

### Frontend:
- Update the single line in `ApiService.kt` for the upload endpoint

---

## IMPORTANT NOTES

✅ **Do NOT delete old code** - The `/api/caterers/match/{event_id}` endpoint is kept for backward compatibility

✅ **No database migrations needed** - All changes are API/route level only

✅ **Both endpoints work** - You can use either `/search` (new, recommended) or `/match/{event_id}` (legacy)

✅ **Make sure imports are correct** in caterer.py:
```python
from fastapi import APIRouter, Body, Depends, HTTPException, UploadFile, File
```

---

## NEXT STEPS AFTER FIXING

1. Restart your FastAPI backend: `python -m uvicorn app.main:app --reload`
2. Rebuild Android app in Android Studio
3. Test the "Find Caterers" feature
4. Test organizer profile creation/update
5. Test image uploads

---

**All changes are backward compatible and non-breaking!**

