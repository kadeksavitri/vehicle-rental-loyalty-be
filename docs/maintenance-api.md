# Maintenance Record API Documentation

## Base URL

```
http://localhost:8080/api/maintenance
```

## Authentication

All endpoints require authentication with JWT Bearer token.

## Authorization

- **SUPERADMIN**: Full access to all maintenance records
- **RENTAL_VENDOR**: Access only to their own maintenance records

---

## Endpoints

### 1. Get All Maintenance Records

Retrieve all maintenance records. Deleted records are excluded.

**Endpoint:** `GET /api/maintenance`

**Authorization:** SUPERADMIN, RENTAL_VENDOR

**Query Parameters:** None

**Request Example:**

```http
GET /api/maintenance HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example (200 OK):**

```json
{
  "status": 200,
  "message": "Data maintenance record berhasil diambil",
  "timestamp": "2025-11-30T15:30:00",
  "data": [
    {
      "id": "MAINT0001",
      "vehicleId": "VEH0001",
      "vehicleDisplay": "VEH0001 - B 1234 XYZ",
      "serviceDate": "2025-11-25T10:00:00",
      "description": "Engine oil change and filter replacement",
      "cost": 500000,
      "vendorNote": "Regular maintenance scheduled",
      "status": "Ongoing",
      "createdAt": "2025-11-25T09:00:00"
    },
    {
      "id": "MAINT0002",
      "vehicleId": "VEH0002",
      "vehicleDisplay": "VEH0002 - B 5678 ABC",
      "serviceDate": "2025-11-28T14:00:00",
      "description": "Brake pad replacement",
      "cost": 750000,
      "vendorNote": "Brake pads worn out",
      "status": "Completed",
      "createdAt": "2025-11-28T13:00:00"
    }
  ]
}
```

**Response Example (500 Internal Server Error):**

```json
{
  "status": 500,
  "message": "Gagal mengambil data maintenance record: <error_message>",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

---

### 2. Get Maintenance Record by ID

Retrieve detailed information of a specific maintenance record.

**Endpoint:** `GET /api/maintenance/{id}`

**Authorization:** SUPERADMIN, RENTAL_VENDOR

**Path Parameters:**

- `id` (string, required): Maintenance record ID

**Request Example:**

```http
GET /api/maintenance/MAINT0001 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example (200 OK):**

```json
{
  "status": 200,
  "message": "Data maintenance record berhasil ditemukan",
  "timestamp": "2025-11-30T15:30:00",
  "data": {
    "id": "MAINT0001",
    "vehicleId": "VEH0001",
    "vehicleDisplay": "VEH0001 - B 1234 XYZ",
    "serviceDate": "2025-11-25T10:00:00",
    "description": "Engine oil change and filter replacement",
    "cost": 500000,
    "vendorNote": "Regular maintenance scheduled",
    "status": "Ongoing",
    "createdAt": "2025-11-25T09:00:00"
  }
}
```

**Response Example (404 Not Found):**

```json
{
  "status": 404,
  "message": "Maintenance record dengan ID MAINT9999 tidak ditemukan",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (403 Forbidden):**

```json
{
  "status": 403,
  "message": "Anda tidak memiliki akses ke maintenance record ini",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Notes:**

- Deleted maintenance records cannot be retrieved (returns 404)
- RENTAL_VENDOR can only access their own records

---

### 3. Create Maintenance Record

Create a new maintenance record for a vehicle.

**Endpoint:** `POST /api/maintenance/create`

**Authorization:** SUPERADMIN, RENTAL_VENDOR

**Request Body:**

```json
{
  "vehicleId": "VEH0001",
  "serviceDate": "2025-12-05T10:00:00",
  "description": "Tire replacement and alignment",
  "cost": 1200000,
  "vendorNote": "All four tires replaced with new ones"
}
```

**Request Body Schema:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| vehicleId | string | Yes | ID of the vehicle |
| serviceDate | datetime | Yes | Scheduled service date and time |
| description | string | No | Description of maintenance work |
| cost | long | No | Cost of maintenance |
| vendorNote | string | No | Additional notes from vendor |

**Request Example:**

```http
POST /api/maintenance/create HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "vehicleId": "VEH0001",
  "serviceDate": "2025-12-05T10:00:00",
  "description": "Tire replacement and alignment",
  "cost": 1200000,
  "vendorNote": "All four tires replaced with new ones"
}
```

**Response Example (201 Created):**

```json
{
  "status": 201,
  "message": "Maintenance record berhasil dibuat",
  "timestamp": "2025-11-30T15:30:00",
  "data": {
    "id": "MAINT0003",
    "vehicleId": "VEH0001",
    "vehicleDisplay": "VEH0001 - B 1234 XYZ",
    "serviceDate": "2025-12-05T10:00:00",
    "description": "Tire replacement and alignment",
    "cost": 1200000,
    "vendorNote": "All four tires replaced with new ones",
    "status": "Ongoing",
    "createdAt": "2025-11-30T15:30:00"
  }
}
```

**Response Example (400 Bad Request - Validation Error):**

```json
{
  "status": 400,
  "message": "vehicleId required; serviceDate required; ",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (400 Bad Request - Vehicle Not Available):**

```json
{
  "status": 400,
  "message": "Vehicle harus berstatus Available untuk membuat maintenance record",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (400 Bad Request - Vehicle Not Found):**

```json
{
  "status": 400,
  "message": "Vehicle dengan ID VEH9999 tidak ditemukan",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Business Rules:**

1. Vehicle ID must exist
2. Vehicle status must be "Available"
3. Upon creation, vehicle status automatically changes to "In Maintenance"
4. Maintenance record status is set to "Ongoing" by default
5. RENTAL_VENDOR can only create records for their own vehicles

---

### 4. Update Maintenance Record

Update an existing maintenance record.

**Endpoint:** `PUT /api/maintenance/update/{id}`

**Authorization:** SUPERADMIN, RENTAL_VENDOR

**Path Parameters:**

- `id` (string, required): Maintenance record ID

**Request Body:**

```json
{
  "id": "MAINT0001",
  "vehicleId": "VEH0001",
  "serviceDate": "2025-12-06T11:00:00",
  "description": "Engine oil change, filter replacement, and spark plug check",
  "cost": 650000,
  "vendorNote": "Added spark plug inspection",
  "status": "Ongoing"
}
```

**Request Body Schema:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | string | Yes | ID of maintenance record (must match path parameter) |
| vehicleId | string | Yes | ID of the vehicle |
| serviceDate | datetime | Yes | Scheduled service date and time |
| description | string | No | Description of maintenance work |
| cost | long | No | Cost of maintenance |
| vendorNote | string | No | Additional notes from vendor |
| status | string | Yes | Status (Ongoing, Completed, etc.) |

**Request Example:**

```http
PUT /api/maintenance/update/MAINT0001 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": "MAINT0001",
  "vehicleId": "VEH0001",
  "serviceDate": "2025-12-06T11:00:00",
  "description": "Engine oil change, filter replacement, and spark plug check",
  "cost": 650000,
  "vendorNote": "Added spark plug inspection",
  "status": "Ongoing"
}
```

**Response Example (200 OK):**

```json
{
  "status": 200,
  "message": "Maintenance record berhasil diperbarui",
  "timestamp": "2025-11-30T15:30:00",
  "data": {
    "id": "MAINT0001",
    "vehicleId": "VEH0001",
    "vehicleDisplay": "VEH0001 - B 1234 XYZ",
    "serviceDate": "2025-12-06T11:00:00",
    "description": "Engine oil change, filter replacement, and spark plug check",
    "cost": 650000,
    "vendorNote": "Added spark plug inspection",
    "status": "Ongoing",
    "createdAt": "2025-11-25T09:00:00"
  }
}
```

**Response Example (400 Bad Request - ID Mismatch):**

```json
{
  "status": 400,
  "message": "ID maintenance record tidak sesuai dengan data body",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (404 Not Found):**

```json
{
  "status": 404,
  "message": "Maintenance record dengan ID MAINT9999 tidak ditemukan",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (400 Bad Request - Unauthorized):**

```json
{
  "status": 400,
  "message": "Gagal memperbarui: Anda tidak memiliki akses untuk mengubah maintenance record ini",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Business Rules:**

1. ID in path must match ID in request body
2. If status is changed to "Completed", vehicle status automatically changes to "Available"
3. RENTAL_VENDOR can only update their own records

---

### 5. Update Maintenance Record Status

Update only the status of a maintenance record.

**Endpoint:** `PUT /api/maintenance/update-status/{id}`

**Authorization:** SUPERADMIN, RENTAL_VENDOR

**Path Parameters:**

- `id` (string, required): Maintenance record ID

**Request Body:**

```json
{
  "status": "Completed"
}
```

**Request Body Schema:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| status | string | Yes | New status (e.g., Completed, Ongoing) |

**Request Example:**

```http
PUT /api/maintenance/update-status/MAINT0001 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "Completed"
}
```

**Response Example (200 OK):**

```json
{
  "status": 200,
  "message": "Status maintenance record berhasil diperbarui",
  "timestamp": "2025-11-30T15:30:00",
  "data": {
    "id": "MAINT0001",
    "vehicleId": "VEH0001",
    "vehicleDisplay": "VEH0001 - B 1234 XYZ",
    "serviceDate": "2025-11-25T10:00:00",
    "description": "Engine oil change and filter replacement",
    "cost": 500000,
    "vendorNote": "Regular maintenance scheduled",
    "status": "Completed",
    "createdAt": "2025-11-25T09:00:00"
  }
}
```

**Response Example (404 Not Found):**

```json
{
  "status": 404,
  "message": "Maintenance record dengan ID MAINT9999 tidak ditemukan",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (400 Bad Request - Missing Field):**

```json
{
  "status": 400,
  "message": "status required; ",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Business Rules:**

1. When status is changed to "Completed", vehicle status automatically changes to "Available"
2. RENTAL_VENDOR can only update their own records

---

### 6. Delete Maintenance Record

Soft delete a maintenance record. The record is not physically removed but marked as deleted.

**Endpoint:** `DELETE /api/maintenance/delete/{id}`

**Authorization:** SUPERADMIN, RENTAL_VENDOR

**Path Parameters:**

- `id` (string, required): Maintenance record ID

**Request Example:**

```http
DELETE /api/maintenance/delete/MAINT0001 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <token>
```

**Response Example (200 OK):**

```json
{
  "status": 200,
  "message": "Maintenance record berhasil dihapus",
  "timestamp": "2025-11-30T15:30:00",
  "data": {
    "id": "MAINT0001",
    "vehicleId": "VEH0001",
    "vehicleDisplay": "VEH0001 - B 1234 XYZ",
    "serviceDate": "2025-11-25T10:00:00",
    "description": "Engine oil change and filter replacement",
    "cost": 500000,
    "vendorNote": "Regular maintenance scheduled",
    "status": "Ongoing",
    "createdAt": "2025-11-25T09:00:00"
  }
}
```

**Response Example (404 Not Found):**

```json
{
  "status": 404,
  "message": "Maintenance record dengan ID MAINT9999 tidak ditemukan",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Response Example (400 Bad Request - Unauthorized):**

```json
{
  "status": 400,
  "message": "Tidak dapat menghapus maintenance record: Anda tidak memiliki akses untuk menghapus maintenance record ini",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Business Rules:**

1. Soft delete mechanism is used (deleted_at timestamp is set)
2. Deleted records do not appear in GET requests
3. RENTAL_VENDOR can only delete their own records

---

## Error Codes

| Status Code | Description                                               |
| ----------- | --------------------------------------------------------- |
| 200         | Success                                                   |
| 201         | Created successfully                                      |
| 400         | Bad Request - Validation error or business rule violation |
| 401         | Unauthorized - Invalid or missing authentication token    |
| 403         | Forbidden - Insufficient permissions                      |
| 404         | Not Found - Resource does not exist or has been deleted   |
| 500         | Internal Server Error                                     |

---

## Common Error Scenarios

### Authentication Errors

**Missing Token:**

```json
{
  "status": 401,
  "message": "Unauthorized - Authentication token required",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

**Invalid Token:**

```json
{
  "status": 401,
  "message": "Unauthorized - Invalid token",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

### Authorization Errors

**Insufficient Permissions:**

```json
{
  "status": 403,
  "message": "Access denied - Insufficient permissions",
  "timestamp": "2025-11-30T15:30:00",
  "data": null
}
```

---

## Data Models

### MaintenanceRecordResponseDTO

```json
{
  "id": "string",
  "vehicleId": "string",
  "vehicleDisplay": "string",
  "serviceDate": "datetime",
  "description": "string",
  "cost": "long",
  "vendorNote": "string",
  "status": "string",
  "createdAt": "datetime"
}
```

### CreateMaintenanceRequestDTO

```json
{
  "vehicleId": "string (required)",
  "serviceDate": "datetime (required)",
  "description": "string (optional)",
  "cost": "long (optional)",
  "vendorNote": "string (optional)"
}
```

### UpdateMaintenanceRequestDTO

```json
{
  "id": "string (required)",
  "vehicleId": "string (required)",
  "serviceDate": "datetime (required)",
  "description": "string (optional)",
  "cost": "long (optional)",
  "vendorNote": "string (optional)",
  "status": "string (required)"
}
```

### UpdateMaintenanceStatusRequestDTO

```json
{
  "status": "string (required)"
}
```

---

## Business Logic Summary

### Vehicle Status Management

- When a maintenance record is created with "Available" vehicle → Vehicle status changes to "In Maintenance"
- When maintenance status changes to "Completed" → Vehicle status changes to "Available"

### Access Control

- **SUPERADMIN**: Can view, create, update, and delete all maintenance records
- **RENTAL_VENDOR**: Can only access maintenance records for vehicles they own

### Soft Delete

- Delete operation sets `deleted_at` timestamp
- Deleted records are excluded from all GET operations
- Deleted records cannot be retrieved or modified

---

## Testing with cURL

### Get All Maintenance Records

```bash
curl -X GET "http://localhost:8080/api/maintenance" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Get Maintenance Record by ID

```bash
curl -X GET "http://localhost:8080/api/maintenance/MAINT0001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Create Maintenance Record

```bash
curl -X POST "http://localhost:8080/api/maintenance/create" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "vehicleId": "VEH0001",
    "serviceDate": "2025-12-05T10:00:00",
    "description": "Tire replacement",
    "cost": 1200000,
    "vendorNote": "All tires replaced"
  }'
```

### Update Maintenance Record

```bash
curl -X PUT "http://localhost:8080/api/maintenance/update/MAINT0001" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "MAINT0001",
    "vehicleId": "VEH0001",
    "serviceDate": "2025-12-06T11:00:00",
    "description": "Updated description",
    "cost": 650000,
    "vendorNote": "Updated note",
    "status": "Ongoing"
  }'
```

### Update Maintenance Status

```bash
curl -X PUT "http://localhost:8080/api/maintenance/update-status/MAINT0001" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Completed"
  }'
```

### Delete Maintenance Record

```bash
curl -X DELETE "http://localhost:8080/api/maintenance/delete/MAINT0001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Notes

- All datetime fields use ISO 8601 format: `YYYY-MM-DDTHH:mm:ss`
- Timezone for responses is Asia/Jakarta
- All monetary values (cost) are in Indonesian Rupiah (IDR)
- ID format: `MAINTxxxx` (e.g., MAINT0001, MAINT0002)
