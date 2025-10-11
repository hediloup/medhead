# MedHead Frontend - Hospital Bed Allocation Interface

This Angular application provides a graphical interface to consume the `/api/allocate` API from the MedHead backend. It allows users to select a medical specialty and enter a location to get a hospital recommendation.

## 🚀 Features

- **Automatic geocoding**: Converts addresses to latitude/longitude coordinates via Nominatim API (OpenStreetMap)
- **Specialty selection**: Intuitive interface to choose from 16 medical specialties
- **Hospital search**: Finds the nearest hospital with available beds
- **Results display**: Shows all important information (name, distance, estimated time, available beds)
- **Responsive design**: Interface adapted for mobile and desktop devices
- **Error handling**: Clear and informative error messages

## 🛠️ Technologies Used

- **Angular 16**: Frontend framework
- **TypeScript**: Development language
- **Reactive Forms**: Form management
- **HttpClient**: Backend API communication
- **CSS3**: Modern styles with animations
- **Nominatim API**: OpenStreetMap geocoding service

## 📋 Prerequisites

- Node.js (version 18 or higher)
- npm (version 9 or higher)
- MedHead backend running on `http://localhost:8080`

## 🔧 Installation and Startup

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Start the application**:
   ```bash
   npm start
   ```

3. **Access the application**:
   Open your browser at: `http://localhost:4200`

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── hospital-allocation.component.ts
│   │   │   ├── hospital-allocation.component.html
│   │   │   └── hospital-allocation.component.css
│   │   ├── services/
│   │   │   ├── allocation.service.ts
│   │   │   └── geocoding.service.ts
│   │   ├── models/
│   │   │   ├── allocation-request.ts
│   │   │   ├── allocation-response.ts
│   │   │   └── geocoding-response.ts
│   │   ├── app.component.ts
│   │   └── app.module.ts
│   ├── styles.css
│   └── index.html
├── package.json
├── angular.json
└── tsconfig.json
```

## 🔄 Workflow

1. **Data entry**: User selects a specialty and enters their address
2. **Geocoding**: Address is converted to coordinates via Nominatim API
3. **Allocation request**: Coordinates and specialty are sent to the backend API
4. **Result**: Recommended hospital is displayed with all relevant information

## 🌐 APIs Used

### Backend API (MedHead)
- **POST /api/allocate**: Hospital allocation request
- **GET /api/health**: API status verification

### External API
- **Nominatim OpenStreetMap**: Address geocoding
  - URL: `https://nominatim.openstreetmap.org/search`
  - Usage: Converting addresses to latitude/longitude coordinates

## 📱 Available Medical Specialties

- Cardiology
- Neurology
- Orthopedics
- Emergency Medicine
- Pediatrics
- Oncology
- Radiology
- Anesthesiology
- Dermatology
- Psychiatry
- Gynecology
- Urology
- Ophthalmology
- ENT
- Internal Medicine
- General Surgery

## 🎨 User Interface

The interface offers:
- **Intuitive form** with real-time validation
- **Loading indicators** during operations
- **Clear and informative error/success messages**
- **Structured and readable results display**
- **Responsive design** adapted to all screens

## 🔧 Configuration

### Backend API URL
The backend API URL is configured in `allocation.service.ts`:
```typescript
private readonly API_BASE_URL = 'http://localhost:8080/api';
```

To change the URL, modify this constant.

### Geocoding Service
The service uses OpenStreetMap's Nominatim API. No API key is required, but it is recommended to respect the terms of use.

## 🚨 Error Handling

The application handles several types of errors:
- **Validation errors**: Missing required fields
- **Geocoding errors**: Address not found
- **Network errors**: Connection problems
- **API errors**: Server-side issues

## 📊 Displayed Data

For each recommended hospital:
- Hospital name
- Requested specialty
- Distance in kilometers
- Estimated travel time
- Number of available beds
- Hospital identifier

## 🧪 Tests

To run existing Cypress tests:
```bash
npm run cy:open  # Graphical interface
npm run cy:run   # Command line tests
```

## 📝 Available Scripts

- `npm start`: Starts the development server
- `npm build`: Compiles the application for production
- `npm test`: Runs unit tests
- `npm run cy:open`: Opens Cypress interface
- `npm run cy:run`: Runs Cypress tests

## 🤝 Contributing

To contribute to the project:
1. Follow Angular code conventions
2. Add tests for new features
3. Document important changes
4. Respect security best practices

## 📄 License

This project is part of the MedHead system and follows the same license conditions.
