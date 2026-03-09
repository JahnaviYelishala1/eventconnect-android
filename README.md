# EventConnect

EventConnect is a platform for event hosts to find caterers, NGOs, and manage event logistics, food prediction, and bookings. The app connects organizers, caterers, and NGOs for seamless event management.

## Features

- User authentication (Signup/Login)
- Organizer dashboard: create/manage events, find caterers, book services
- Caterer dashboard: profile management, service listing, booking management
- NGO dashboard: registration, document upload, event participation
- Food prediction: AI-powered food quantity estimation
- Real-time chat between organizers and caterers
- Booking management and payment

## API Endpoints

### Authentication
- `POST /api/users/signup` — Register a new user
- `POST /api/users/login` — Login user
- `POST /api/users/select-role` — Select user role (organizer/caterer/ngo)

### Organizer APIs
- `POST /api/events` — Create a new event
- `GET /api/events/my-events` — Get organizer's events
- `PATCH /api/events/{eventId}/complete` — Complete an event
- `POST /api/events/predict-food` — Predict food quantity for event
- `GET /api/bookings/event/{eventId}` — Get bookings for an event

### Caterer APIs
- `POST /api/caterers/profile` — Create caterer profile
- `GET /api/caterers/profile` — Get caterer profile
- `PUT /api/caterers/profile` — Update caterer profile
- `POST /api/caterers/upload-image` — Upload caterer profile image
- `GET /api/caterers/match/{event_id}` — Find matching caterers for event
- `POST /api/caterers/book/{event_id}/{caterer_id}` — Book a caterer for event

### NGO APIs
- `POST /api/ngos/register` — Register NGO
- `GET /api/ngos/profile/me` — Get NGO profile
- `POST /api/ngos/upload-image` — Upload NGO profile image
- `POST /api/ngos/documents` — Upload NGO documents

### Chat APIs
- `GET /api/chat/{booking_id}` — Get chat history for booking
- `WS /api/ws/chat/{booking_id}` — WebSocket for real-time chat

### Food Prediction
- `POST /api/events/predict-food` — AI-powered food quantity prediction

## Technology Stack

- **Frontend:** Android (Jetpack Compose)
- **Backend:** FastAPI (Python)
- **Database:** PostgreSQL
- **Cloud Storage:** Cloudinary (for images)
- **Authentication:** Firebase Auth
- **AI/ML:** Food prediction model (Python)

## How APIs Are Used

- **Authentication:** Secure user login/signup, role selection
- **Organizer:** Create/manage events, predict food, find caterers, book services
- **Caterer:** Manage profile, upload images, list services, accept bookings
- **NGO:** Register, upload documents, participate in events
- **Chat:** Real-time communication between organizer and caterer
- **Food Prediction:** Estimate food quantity based on event details

## Setup Instructions

1. Clone the repository
2. Configure Firebase and Cloudinary keys in backend
3. Run backend server (FastAPI)
4. Build and run Android app

## Contributors
- Jahnavi Yelishala
- ...

## License
MIT

