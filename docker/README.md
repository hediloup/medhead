# Docker Configuration - MedHead

This folder contains the complete Docker configuration to launch the entire MedHead application stack:
- PostgreSQL database with real UK hospital data
- Spring Boot backend API
- Angular frontend application
- pgAdmin for database administration

## 🏥 Included Data

The PostgreSQL database contains real UK hospital data with:
- **34 hospitals** distributed across the UK (England, Scotland, Wales, Northern Ireland)
- **33 medical specialties** based on NHS standards
- **Precise GPS coordinates** for each hospital
- **Complete addresses** and detailed information
- **Number of available beds** per hospital
- **Major cities**: London, Manchester, Birmingham, Leeds, Liverpool, Newcastle, Bristol, Sheffield, Nottingham, Leicester, Cardiff, Edinburgh, Glasgow, Belfast

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- Ports 4200, 5433, 8080, and 8082 available

### Starting the complete application

```bash
# From the docker folder
cd docker

# Start all services (database, backend, frontend, pgAdmin)
./start-medhead.sh

# Or manually:
docker-compose up --build -d

# Check that services are started
docker-compose ps
```

### Stopping services

```bash
# Stop services
docker-compose down

# Stop and remove volumes (WARNING: deletes data)
docker-compose down -v
```

## 📊 Service Access

### Frontend Angular
- **URL**: http://localhost:4200
- **Description**: Interface utilisateur pour l'allocation d'Lits d'Hôpital

### Backend API
- **URL**: http://localhost:8080
- **API Health**: http://localhost:8080/api/health
- **Description**: API REST Spring Boot

### PostgreSQL Database
- **Host**: localhost
- **Port**: 5433
- **Database**: medhead_db
- **Username**: medhead_user
- **Password**: medhead_password

### pgAdmin (web administration interface)
- **URL**: http://localhost:8082
- **Email**: admin@medhead.com
- **Password**: admin123

To connect pgAdmin to PostgreSQL:
1. Open pgAdmin
2. Right-click on "Servers" → "Create" → "Server"
3. "General" tab: name = "MedHead PostgreSQL"
4. "Connection" tab:
   - Host: `postgres` (Docker service name)
   - Port: 5432
   - Database: medhead_db
   - Username: medhead_user
   - Password: medhead_password

## 🔧 Application Configuration

### To use PostgreSQL with the API

1. **Start PostgreSQL**:
```bash
cd docker
docker-compose up -d postgres
```

2. **Launch the application with production profile**:
```bash
cd ../backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

The API will be available at: http://localhost:8082

### API Testing

Once the application is started, you can test the API:

**POST** `/api/allocate`
```bash
curl -X POST http://localhost:8082/api/allocate \
  -H "Content-Type: application/json" \
  -d '{
    "specialty": "Cardiology",
    "latitude": 51.5074,
    "longitude": -0.1278
  }'
```

**GET** `/api/allocate`
```bash
curl "http://localhost:8082/api/allocate?specialty=Cardiology&latitude=51.5074&longitude=-0.1278"
```

### Available profiles

- **dev** (default): H2 in-memory with test data
- **prod**: PostgreSQL with real data

## 📁 File Structure

```
docker/
├── docker-compose.yml          # Docker Compose configuration
├── postgres.conf              # Optimized PostgreSQL configuration
├── init-scripts/
│   └── 01-init-database.sql   # Initialization script with real data
└── README.md                  # This file
```

## 🗄️ Database

### specialities table

| Column | Type | Description |
|---------|------|-------------|
| id | BIGSERIAL | Unique identifier |
| name | VARCHAR(255) | Specialty name |
| description | TEXT | Specialty description |
| created_at | TIMESTAMP | Creation date |

### hospitals table

| Column | Type | Description |
|---------|------|-------------|
| id | BIGSERIAL | Unique identifier |
| name | VARCHAR(255) | Hospital name |
| latitude | DOUBLE PRECISION | GPS latitude |
| longitude | DOUBLE PRECISION | GPS longitude |
| city | VARCHAR(255) | Hospital city |
| address | TEXT | Complete address |
| available_beds | INTEGER | Number of available beds |
| created_at | TIMESTAMP | Creation date |
| updated_at | TIMESTAMP | Last update date |

### hospital_specialities table (junction table)

| Column | Type | Description |
|---------|------|-------------|
| hospital_id | BIGINT | Reference to hospitals.id |
| speciality_id | BIGINT | Reference to specialities.id |

### Created indexes

- `idx_hospitals_location`: Optimizes geospatial queries
- `idx_hospitals_city`: Index on city
- `idx_hospital_specialities_hospital`: Index on hospital_id
- `idx_hospital_specialities_speciality`: Index on speciality_id

## 🔍 Useful Queries

### List all specialties
```sql
SELECT id, name, description 
FROM specialities 
ORDER BY name;
```

### List all hospitals with their specialties
```sql
SELECT h.id, h.name, h.city, h.available_beds, 
       STRING_AGG(s.name, ', ') as specialities
FROM hospitals h
LEFT JOIN hospital_specialities hs ON h.id = hs.hospital_id
LEFT JOIN specialities s ON hs.speciality_id = s.id
GROUP BY h.id, h.name, h.city, h.available_beds
ORDER BY h.name;
```

### Find hospitals by specialty
```sql
SELECT h.name, h.city, h.available_beds, s.name as speciality
FROM hospitals h
JOIN hospital_specialities hs ON h.id = hs.hospital_id
JOIN specialities s ON hs.speciality_id = s.id
WHERE s.name = 'Cardiology' 
AND h.available_beds > 0;
```

### Hospitals near a position (example: London)
```sql
SELECT h.name, h.city,
       (6371 * acos(cos(radians(51.5074)) * cos(radians(h.latitude)) * 
        cos(radians(h.longitude) - radians(-0.1278)) + 
        sin(radians(51.5074)) * sin(radians(h.latitude)))) AS distance_km
FROM hospitals h
JOIN hospital_specialities hs ON h.id = hs.hospital_id
JOIN specialities s ON hs.speciality_id = s.id
WHERE s.name = 'Cardiology' 
AND h.available_beds > 0
ORDER BY distance_km 
LIMIT 5;
```

### Specialties available in a city
```sql
SELECT DISTINCT s.name
FROM specialities s
JOIN hospital_specialities hs ON s.id = hs.speciality_id
JOIN hospitals h ON hs.hospital_id = h.id
WHERE h.city = 'London'
ORDER BY s.name;
```

## 🛠️ Maintenance

### Backup the database
```bash
docker-compose exec postgres pg_dump -U medhead_user medhead_db > backup.sql
```

### Restore the database
```bash
docker-compose exec -T postgres psql -U medhead_user medhead_db < backup.sql
```

### Check logs
```bash
# PostgreSQL logs
docker-compose logs postgres

# pgAdmin logs
docker-compose logs pgadmin

# Real-time logs
docker-compose logs -f postgres
```

## 🐛 Troubleshooting

### Port 5432 is already in use
```bash
# Find the process using the port
ss -tulpn | grep :5432

# Port has been changed to 5433 in docker-compose.yml
ports:
  - "5433:5432"  # Use port 5433 instead of 5432
```

### Database won't start
```bash
# Check logs
docker-compose logs postgres

# Remove volumes and restart
docker-compose down -v
docker-compose up -d
```

### Complete reset
```bash
# Stop and remove everything
docker-compose down -v
docker system prune -f

# Restart
docker-compose up -d
```

## 📝 Important Notes

- Data is persistent thanks to Docker volumes
- Initialization script only runs on first startup
- PostgreSQL configuration is optimized for development
- H2 remains available for tests (`dev` profile)

## 🔒 Security

⚠️ **Warning**: This configuration is for development only. For production, modify:
- Default passwords
- PostgreSQL security configuration
- Connection parameters
- Enable SSL/TLS
