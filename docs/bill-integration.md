# Bill Service Integration Documentation

## Overview

The Vehicle Rental system integrates with the Bill Service to manage payment status for rental bookings. When a customer creates a booking, a corresponding bill is automatically created in the Bill Service.

## Architecture

### Components Created

1. **DTOs**:

   - `CreateBillRequestDTO` - Request to create bill
   - `BillResponseDTO` - Bill information from bill service
   - `BillApiResponseDTO` - Wrapper for bill service API responses

2. **Service Layer**:

   - `BillService` (interface)
   - `BillServiceImpl` - REST client to communicate with bill service

3. **Model Updates**:

   - Added `BillStatus` enum to `RentalBooking`: `UNPAID`, `PAID`
   - Added `billStatus` field to `RentalBooking` entity

4. **Configuration**:
   - Added bill service URL configuration in `application-dev.yaml` and `application-prod.yaml`

## Integration Flow

### 1. Booking Creation Flow

```
Customer creates booking
    ↓
Booking saved to database (billStatus = UNPAID)
    ↓
POST /api/bill/create → Bill Service
    ↓
GET /api/bill/customer → Bill Service (to get updated status)
    ↓
Update booking billStatus based on bill service response
    ↓
Return booking response with billStatus
```

### 2. Bill Service Endpoints Used

#### POST /api/bill/create

Creates a bill for the booking.

**Request:**

```json
{
  "customerId": "CUST-0001",
  "serviceName": "Vehicle Rental",
  "serviceReferenceId": "VR000001",
  "description": "Vehicle Rental Booking - Toyota Avanza",
  "amount": 1500000
}
```

**Response:**

```json
{
  "status": 200,
  "message": "Bill created",
  "data": {
    "id": "uuid",
    "customerId": "CUST-0001",
    "serviceName": "Vehicle Rental",
    "serviceReferenceId": "VR000001",
    "status": "UNPAID",
    "amount": 1500000
  }
}
```

#### GET /api/bill/customer

Retrieves all bills for a customer to check payment status.

**Query Parameters:**

- `customerId` (string) - Customer ID

**Response:**

```json
{
  "status": 200,
  "message": "Bills retrieved",
  "data": [
    {
      "id": "uuid",
      "customerId": "CUST-0001",
      "serviceReferenceId": "VR000001",
      "status": "PAID",
      "amount": 1500000
    }
  ]
}
```

## Configuration

### Environment Variables

Add to your `.env` file:

```properties
# Bill Service Configuration
BILL_SERVICE_BASE_URL=http://localhost:8081
```

### Application Configuration

**application-dev.yaml:**

```yaml
bill:
  service:
    base-url: ${BILL_SERVICE_BASE_URL:http://localhost:8081}
```

**application-prod.yaml:**

```yaml
bill:
  service:
    base-url: ${BILL_SERVICE_BASE_URL:http://bill-service.production.com}
```

## Error Handling

The integration is designed to be resilient:

1. **Bill Creation Failure**: If the bill service is unavailable or returns an error, the booking is still created with `billStatus = UNPAID`
2. **Logging**: All bill service calls are logged for debugging
3. **Graceful Degradation**: Booking creation continues even if bill integration fails

## Database Schema Changes

### RentalBooking Table

New column added:

```sql
ALTER TABLE rentalBookings
ADD COLUMN bill_status VARCHAR(10) NOT NULL DEFAULT 'UNPAID';
```

Values: `UNPAID`, `PAID`

## API Response Changes

### RentalBookingResponseDTO

New field added:

```json
{
  "id": "VR000001",
  "customerId": "CUST-0001",
  "status": "Upcoming",
  "billStatus": "UNPAID", // ← NEW FIELD
  "totalPrice": 1500000.0
}
```

## Testing

### Manual Testing

1. **Start Bill Service** (on port 8081 or configured port)

2. **Create a Booking:**

```bash
curl -X POST "http://localhost:8080/api/rental-bookings/create" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-0001",
    "vehicleId": "VEH0001",
    "pickUpTime": "2025-12-05T10:00:00",
    "dropOffTime": "2025-12-07T10:00:00",
    "pickUpLocation": "Jakarta Pusat",
    "dropOffLocation": "Jakarta Pusat",
    "capacityNeeded": 5,
    "transmissionNeeded": "Automatic",
    "includeDriver": false,
    "listOfAddOns": []
  }'
```

3. **Verify Response** includes `billStatus` field

4. **Check Bill Service:**

```bash
curl -X GET "http://localhost:8081/api/bill/customer?customerId=CUST-0001" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Expected Behavior

- ✅ Booking created successfully
- ✅ Bill created in bill service
- ✅ `billStatus` field populated in booking response
- ✅ `serviceReferenceId` in bill matches booking ID

## Troubleshooting

### Bill Service Connection Issues

**Problem:** Cannot connect to bill service

**Solutions:**

1. Verify bill service is running: `curl http://localhost:8081/actuator/health`
2. Check `BILL_SERVICE_BASE_URL` configuration
3. Review application logs for connection errors

### Bill Status Not Updating

**Problem:** `billStatus` remains `UNPAID` even after payment

**Solutions:**

1. Bill status is set during booking creation only
2. Implement a scheduled job or webhook to sync bill status periodically
3. Add manual "Refresh Bill Status" endpoint

### Logs to Check

```bash
# Search for bill service calls
grep "Creating bill" application.log
grep "Bill created successfully" application.log
grep "Failed to create bill" application.log
```

## Future Enhancements

1. **Webhook Integration**: Receive real-time updates from bill service when payment status changes
2. **Scheduled Sync**: Periodically check and update bill status for unpaid bookings
3. **Retry Mechanism**: Implement retry logic for failed bill creation
4. **Circuit Breaker**: Add resilience pattern for bill service calls
5. **Manual Status Update**: Add endpoint to manually refresh bill status

## Code Example: Manual Bill Status Refresh

```java
@PutMapping("/update-bill-status/{id}")
public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBillStatus(
        @PathVariable String id) {

    RentalBooking booking = rentalBookingRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

    List<BillResponseDTO> bills = billService.getCustomerBills(booking.getCustomerId());
    Optional<BillResponseDTO> bill = bills.stream()
        .filter(b -> id.equals(b.getServiceReferenceId()))
        .findFirst();

    if (bill.isPresent()) {
        booking.setBillStatus(
            "PAID".equals(bill.get().getStatus())
                ? RentalBooking.BillStatus.PAID
                : RentalBooking.BillStatus.UNPAID
        );
        rentalBookingRepository.save(booking);
    }

    return ResponseEntity.ok(/* response */);
}
```

## Security Considerations

1. **Authentication**: Ensure proper JWT token is passed to bill service
2. **Authorization**: Verify customer can only create bills for themselves
3. **Data Validation**: Validate all bill amounts match booking totals
4. **Audit Logging**: Log all bill-related operations for compliance

## Monitoring

Key metrics to monitor:

- Bill service response time
- Bill creation success rate
- Bill status sync frequency
- Failed bill creation count

## References

- Bill Service API Documentation: See bill service docs
- Rental Booking API: `docs/rental-booking-api.md`
- RestTemplate Configuration: `RestTemplateConfig.java`
