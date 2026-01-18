# Functional Requirements

## For unauthenticated users:

* **FR-1.1.** The system shall provide donor registration (minimum: full name, phone number or email, password).
* **FR-1.2.** The system shall provide authentication (email/phone + password).
* **FR-1.3.** The system shall provide password recovery.
* **FR-1.4.** The system shall provide access to general information (donation rules / contacts / preparation guidelines).

### For authenticated donors:

* **FR-2.1.** The system shall allow donors to view and edit their profile.
* **FR-2.2.** The system shall allow donors to schedule a donation appointment (select date/time from available slots).
* **FR-2.3.** The system shall allow donors to cancel or reschedule an appointment.
* **FR-2.4.** The system shall allow donors to schedule a medical examination.
* **FR-2.5.** The system shall allow donors to fill in a consent form (with storage of consent proof and timestamp).
* **FR-2.6.** The system shall allow donors to view their donation history and test results.
* **FR-2.7.** The system shall display the donor’s current eligibility/deferral status and deferral period (if applicable).
* **FR-2.8.** The system shall send reminders to donors about upcoming visits and eligibility for repeat donation.

### For administrators:

* **FR-3.1.** The system shall generate and send reminders to donors about eligibility for repeat donation, and provide an administrator interface with a list of donors to be notified by phone and the ability to mark them as notified.
* **FR-3.2.** The system shall provide an interface for registering new donors by phone via an administrator.
* **FR-3.3.** The system shall notify donors about outdated information/documents in their account and provide an administrator interface with a list of such donors and the ability to mark them as notified.

### For nurses/doctors:

* **FR-4.1.** The system shall allow medical staff to view the list of scheduled donors.
* **FR-4.2.** The system shall allow medical staff to perform eligibility checks (parameters: weight, hemoglobin, blood pressure) and store the results.
* **FR-4.3.** The system shall allow recording of eligibility decisions (eligible / not eligible) and, in case of ineligibility, the deferral period and reason.
* **FR-4.4.** The system shall allow registration of the blood donation event linked to a donor and visit.
* **FR-4.5.** The system shall allow registration of sample(s) and linking a sample to a specific donation.
* **FR-4.6.** The system shall allow registration of adverse reactions.
* **FR-4.7.** The system shall allow staff to change the status of a donor’s application.

### For laboratory technicians:

* **FR-5.1.** The system shall provide a list of registered samples awaiting laboratory testing.
* **FR-5.2.** The system shall allow entering laboratory test results (infections, blood group, Rh factor) for a sample/donation.
* **FR-5.3.** The system shall support publishing test results in the donor’s personal account.

---

## Non-Functional Requirements

### Usability Requirements:

* **UR-001.** The system shall provide an interface based on standard web patterns: navigation menu/section panel, input forms with labels for each field, and confirmation for critical actions.
* **UR-002.** The system shall use a unified message format for Success / Info / Warning / Error.
* **UR-003.** The system shall provide navigation available in all major sections and ensure user orientation: the current section shall be visually highlighted, links and menu items shall have unambiguous names, and the user shall be able to reach any role-available section in no more than 3 steps.
* **UR-004.** The system shall be responsive and adapted for devices with different screen resolutions (mobile: 320px–768px, tablets: 769px–1024px, desktop: 1025px+).
* **UR-005.** The system shall separate the interface into subsystems: donor portal / reception desk / procedure room / laboratory / management.
* **UR-006.** The system shall provide a web interface compatible with the latest versions of Chrome 133+, Firefox 135+, Safari, and Edge 133+.
* **UR-007.** The system shall update search results in real time when filters are applied or changed without page reload.

### Reliability Requirements:

* **RR-001.** The system shall be available 99.9% of the time per year (maximum downtime — 8 hours 45 minutes per year).
* **RR-002.** Scheduled maintenance shall be performed during periods of minimal load and no more than once per month.
* **RR-003.** Mean Time Between Failures (MTBF) shall be at least 1000 hours for critical components. For non-critical components, MTBF ≥ 500 hours.
* **RR-004.** Mean Time To Repair (MTTR) shall not exceed 1 hour for critical failures. For non-critical components, MTTR ≤ 4 hours.
* **RR-005.** The system shall provide secure data transmission via HTTPS.
* **RR-006.** The system shall control access to protected resources using JWT tokens.
* **RR-007.** User data shall be encrypted using the bcrypt algorithm and stored on servers with SSL security certificates.

### Performance Requirements:

* **PR-001.** Maximum response time for user operations — 2 seconds with a 100 Mbps internet connection.
* **PR-002.** Average response time across all operations — 500 ms with a 100 Mbps internet connection.
* **PR-003.** Page load time — ≤ 1.5 seconds for desktop, ≤ 3 seconds for mobile devices (on 3G connection).
* **PR-004.** The platform shall support at least 100 concurrent active users.
* **PR-005.** Asynchronous processing of non-critical operations (e.g., sending email notifications).
