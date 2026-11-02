# Courier Logistics Management System - Spring Boot Backend

A backend-only Spring Boot REST API project implemented from the supplied six-page photographed requirements. **There is no UI or frontend**: no HTML, CSS, templates, dashboard, Swagger UI, or embedded database console.

## What it covers

- Customer management with unique email and contact number.
- Delivery-agent management with unique contact and vehicle number, availability checks, rating search, and rating sorting.
- Warehouse management with unique contact number, location/capacity lookup, and capacity sorting.
- Shipment creation, tracking-number lookup, status updates, destination updates, warehouse assignment, delivery-agent assignment, shipment filtering, sorting, and deletion.
- Package entity lookup by type and listing of all packages.
- Payment listing, lookup by ID/customer, and payment-status updates.
- Tracking history listing, lookup by ID/tracking number/status/shipment.
- Database relationships, request validation, centralized JSON errors, and transaction boundaries for multi-entity operations.

## Technology

- Java 21
- Spring Boot 3.4.5
- Spring Web
- Spring Data JPA / Hibernate
- Jakarta Bean Validation
- H2 file database by default
- Optional MySQL profile
- Maven

## Run locally

1. Install JDK 21 and Maven 3.9 or newer.
2. Extract the ZIP.
3. Open a terminal in the `courier-project` folder (the same folder that contains `pom.xml`).
4. Run:

   ```bash
   mvn spring-boot:run
   ```

5. The API base URL is `http://localhost:8080/api`.

The application creates/updates its development schema automatically. The default H2 file database is written to `./data/courierdb`; data persists between restarts. The H2 web console is disabled so the project remains API-only.

### Use MySQL instead

1. Install and start MySQL.
2. Set environment variables for the connection:

   ```bash
   DB_URL=jdbc:mysql://localhost:3306/courier_logistics?createDatabaseIfNotExist=true
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

3. Run with the MySQL profile:

   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

Change the username/password to match your MySQL installation. Do not commit real credentials.

## Main API routes

All bodies and responses are JSON. Enum values are case-sensitive uppercase values. Full runnable examples are in `api-tests/requests.http`.

### Customers

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/customers` | Create customer |
| GET | `/api/customers` | List all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| GET | `/api/customers/by-email?email=...` | Find customer by email |
| GET | `/api/customers/by-contact?contact=...` | Find customer by contact |
| GET | `/api/customers/by-payment-method?method=UPI&sortBy=name&direction=asc` | Find customers with matching payment method; sort by name/email |
| PUT | `/api/customers/{id}` | Update customer details |
| DELETE | `/api/customers/{id}` | Delete customer, subject to shipment rule |

Customer example:

```json
{"name":"Asha Nair","email":"asha@example.com","contact":"9876543210","address":"Kasaragod, Kerala"}
```

### Delivery agents

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/delivery-agents` | Create agent |
| GET | `/api/delivery-agents` | List all agents |
| GET | `/api/delivery-agents/{id}` | Get by ID |
| GET | `/api/delivery-agents/available` | List available agents |
| GET | `/api/delivery-agents/by-vehicle?vehicleNo=KA01AB1234` | Find by vehicle number |
| GET | `/api/delivery-agents/by-contact?contact=9876501234` | Find by contact |
| GET | `/api/delivery-agents/by-min-rating?rating=4` | Get rating greater than or equal to a value |
| GET | `/api/delivery-agents/sort-by-rating?direction=desc` | Sort by rating (`asc` or `desc`) |
| PUT | `/api/delivery-agents/{id}` | Update agent details |
| PATCH | `/api/delivery-agents/{id}/availability?status=UNAVAILABLE` | Update availability |
| DELETE | `/api/delivery-agents/{id}` | Delete when no shipment references the agent |

Availability values: `AVAILABLE`, `ASSIGNED`, `UNAVAILABLE`, `ON_LEAVE`.

### Warehouses

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/warehouses` | Create warehouse |
| GET | `/api/warehouses` | List all warehouses |
| GET | `/api/warehouses/{id}` | Get by ID |
| GET | `/api/warehouses/by-location?location=Bengaluru` | Search location |
| GET | `/api/warehouses/by-capacity?capacity=500` | Find by exact capacity |
| GET | `/api/warehouses/sort-by-capacity?direction=asc` | Sort capacity (`asc` or `desc`) |
| PUT | `/api/warehouses/{id}` | Update warehouse |
| DELETE | `/api/warehouses/{id}` | Delete when no shipment references the warehouse |

### Shipments

| Method | Route | Purpose |
|---|---|---|
| POST | `/api/shipments` | Create shipment, package, payment, and initial tracking event |
| GET | `/api/shipments` | List shipments |
| GET | `/api/shipments/{id}` | Get shipment by ID |
| GET | `/api/shipments/tracking/{trackingNumber}` | Find shipment by tracking number |
| PATCH | `/api/shipments/{id}/status` | Update status and record tracking history |
| PATCH | `/api/shipments/{id}/assign-agent` | Assign an available delivery agent |
| PATCH | `/api/shipments/{id}/assign-warehouse` | Assign/change warehouse |
| PATCH | `/api/shipments/{id}/destination` | Update destination |
| GET | `/api/shipments/{id}/history` | Get tracking timeline for a shipment |
| GET | `/api/shipments/by-customer/{customerId}` | List a customer's shipments |
| GET | `/api/shipments/by-warehouse/{warehouseId}` | Filter by warehouse |
| GET | `/api/shipments/by-agent/{agentId}` | Filter by delivery agent |
| GET | `/api/shipments/by-route?source=Kasaragod&destination=Bengaluru` | Filter by source and destination |
| GET | `/api/shipments/by-source?source=Kasaragod` | Search source |
| GET | `/api/shipments/by-destination?destination=Bengaluru` | Search destination |
| GET | `/api/shipments/by-delivery-date?date=2026-10-30` | Filter by delivery date (ISO date) |
| GET | `/api/shipments/by-package-type?packageType=BOX` | Filter by package type |
| GET | `/api/shipments/by-status?status=IN_TRANSIT` | Filter by status |
| GET | `/api/shipments/sort-by-weight?direction=asc` | Sort by weight (`asc` or `desc`) |
| DELETE | `/api/shipments/{id}` | Delete shipment and its package/payment/tracking records |

Example shipment creation request (create the customer, warehouse, and agent first; use their returned IDs):

```json
{
  "source":"Kasaragod, Kerala",
  "destination":"Bengaluru, Karnataka",
  "weight":2.5,
  "deliveryDate":"2026-10-30",
  "customerId":1,
  "warehouseId":1,
  "deliveryAgentId":1,
  "packageType":"BOX",
  "fragile":false,
  "length":20,
  "breadth":15,
  "height":10,
  "paymentMethod":"UPI",
  "initialLocation":"Kasaragod"
}
```

Shipment status values: `ORDER_PLACED`, `PICKED_UP`, `IN_TRANSIT`, `ARRIVED_AT_WAREHOUSE`, `OUT_FOR_DELIVERY`, `DELIVERED`, `DELAYED`, `CANCELLED`, `RETURNED`.

### Packages

| Method | Route | Purpose |
|---|---|---|
| GET | `/api/packages` | List package entities |
| GET | `/api/packages/{id}` | Find package by ID |
| GET | `/api/packages/by-type?packageType=BOX` | Find packages by type |

Package records are created when a shipment is created, as specified by the entity relationship model.

### Payments

| Method | Route | Purpose |
|---|---|---|
| GET | `/api/payments` | List payments |
| GET | `/api/payments/{id}` | Find payment by ID |
| GET | `/api/payments/by-customer/{customerId}` | Find payments belonging to a customer's shipments |
| PATCH | `/api/payments/{id}/status?status=PAID` | Update payment status |

Payment methods: `CASH`, `CARD`, `UPI`, `NET_BANKING`, `WALLET`, `COD`. Payment statuses: `PENDING`, `PAID`, `FAILED`, `REFUNDED`.

### Tracking history

| Method | Route | Purpose |
|---|---|---|
| GET | `/api/tracking-history` | List all tracking events chronologically |
| GET | `/api/tracking-history/{id}` | Find event by ID |
| GET | `/api/tracking-history/by-tracking-number?trackingNumber=CR-...` | Find events by shipment tracking number |
| GET | `/api/tracking-history/by-status?status=IN_TRANSIT` | Find events by status |
| GET | `/api/tracking-history/shipment/{shipmentId}` | Find events for one shipment |

## Business rules implemented

1. Email, tracking number, vehicle number, and contact values are unique in their relevant records. Contact numbers are exactly 10 digits, and the same contact cannot be reused between a customer, delivery agent, or warehouse.
2. A customer cannot be deleted while any shipment is pending or undelivered. Once all of that customer's shipments are terminal (`DELIVERED`, `CANCELLED`, or `RETURNED`), deleting the customer also deletes those terminal shipment records and their related packages, payments, and tracking history to preserve relational integrity.
3. A delivery agent cannot be deleted while any shipment references them.
4. A warehouse cannot be deleted while any shipment references it.
5. A shipment requires a customer, warehouse, delivery agent, payment, package entity, and tracking history. The delivery agent must be available when a shipment is created or assigned.
6. Agent availability is updated to `ASSIGNED` when a shipment is assigned, and set back to `AVAILABLE` when the shipment becomes terminal.
7. A default sample price is calculated from weight: ₹80 base, plus ₹25 for each kilogram above 1 kg, plus ₹50 for a fragile package. The supplied requirements mention weight-based pricing and an extra fragile-item charge but do not specify a tariff table, so these rates are documented implementation defaults. An explicit non-negative `paymentAmount` can be passed if your assignment defines a different tariff.
8. Shipment creation and status/destination/assignment updates write tracking history events in the same transaction.

## Validation and error format

Invalid request fields return HTTP 400; unknown IDs return 404; duplicate values, unavailable agents, and prohibited deletes return 409. Unexpected server errors return a generic 500 response. Errors are JSON with `timestamp`, `status`, `error`, `message`, and, for request validation, `details`.

## Tests

Run:

```bash
mvn test
```

The included Spring context and MockMvc tests verify that the application starts and basic API endpoints respond. This workspace did not have Maven installed and external dependency download was unavailable, so these tests could not be executed here; run the command locally to verify the runtime environment.

## Project layout

```text
courier-project/
  pom.xml
  README.md
  api-tests/requests.http
  src/main/java/com/courier/logistics/
    controller/   REST controllers
    dto/          request DTOs
    entity/       JPA entities and enums
    exception/    centralized exception response handling
    repository/   Spring Data repositories
    service/      business logic and transaction boundaries
  src/main/resources/
    application.properties
    application-mysql.properties
  src/test/java/  basic context/API tests
```
