Loyalty Service — RBAC Summary

Endpoints (base `/api/loyalty`):

- GET `/points/{customerId}`
  - Roles allowed: Customer
  - Purpose: Return loyalty points for the specified customer.

- POST `/points/add/{customerId}`
  - Roles allowed: API_KEY (internal service calls such as Billing)
  - Purpose: Add loyalty points to a customer's account.

- POST `/coupons`
  - Roles allowed: Superadmin
  - Purpose: Create a new coupon (payload: name, description, points, percentOff).

- GET `/coupons`
  - Roles allowed: Superadmin, Customer
  - Purpose: List all coupons in system. Response includes id, name, description, points, percentOff.

- PUT `/coupons/{couponId}`
  - Roles allowed: Superadmin
  - Purpose: Update coupon fields (all except coupon id).

- POST `/purchase/{customerId}`
  - Roles allowed: Customer
  - Purpose: Customer purchases a coupon using loyalty points. Validates sufficient points and reduces customer's points.
  - Notes: Generates `PurchasedCoupon` with unique id and code format: `[5-char coupon name]-[5-char customer name]-[current purchased count]`.

- POST `/use`
  - Roles allowed: API_KEY (Billing Service)
  - Purpose: Validate and mark a purchased coupon as used. Payload: purchased coupon code and customer id.
  - Response: `percentOff` (integer). Returns `0` if coupon is invalid or already used.

- GET `/purchased/{customerId}`
  - Roles allowed: Customer
  - Purpose: Return list of purchased coupons owned by the specified customer.

Notes about role naming and mapping:
- Backend maps incoming profile roles to Spring authorities as `ROLE_<UPPERCASE_ROLE_WITH_UNDERSCORES>`.
  - Example: profile role `Customer` -> authority `ROLE_CUSTOMER`.
- API-key requests are authenticated by the `ApiKeyAuthFilter` which grants `ROLE_API_KEY` when the configured header matches.

Operational guidance:
- Ensure the configured `vehicle.api-key` property is kept secret and available to the Billing Service so it can call `POST /points/add` and `POST /use`.
- The Profile Service must return a `role` field (e.g., `Customer`, `Superadmin`, `Rental Vendor`) for the JWT validation endpoint used by the backend.

If you'd like, I can:
- Add integration smoke tests to exercise each of the above RBAC paths (login as different roles, purchase coupon, call use with API key), or
- Start the backend and run quick manual smoke requests now.
