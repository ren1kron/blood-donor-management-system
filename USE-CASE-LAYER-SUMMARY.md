# Use Case Layer Refactoring Summary

## Обзор

Выполнен рефакторинг архитектуры с "толстых сервисов" на Clean Architecture с use case слоем.

**Принцип:** Каждый use case представляет одно бизнес-действие с чётко определённым входом (Command) и выходом (Result).

**Реализация:** Use case реализации делегируют работу существующим сервисам, которые теперь выступают как internal helpers.

---

## Созданные Use Cases по модулям

### Auth (3 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `RegisterUserUseCase` | `RegisterUserCommand` | `AuthResult` |
| `LoginUserUseCase` | `LoginUserCommand` | `AuthResult` |
| `GetCurrentProfileUseCase` | `UUID accountId` | `ProfileResult` |

### Donor (7 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `GetDonorProfileUseCase` | `UUID accountId` | `DonorProfileResult` |
| `UpdateDonorProfileUseCase` | `UpdateDonorProfileCommand` | `DonorProfileResult` |
| `SubmitConsentUseCase` | `SubmitConsentCommand` | `ConsentResult` |
| `ListDonationHistoryUseCase` | `UUID accountId` | `List<DonationHistoryResult>` |
| `CheckEligibilityUseCase` | `UUID accountId` | `EligibilityResult` |
| `ListDonorNotificationsUseCase` | `UUID accountId` | `List<NotificationResult>` |
| `AcknowledgeNotificationUseCase` | `UUID accountId, UUID deliveryId` | `void` |

### Appointment (6 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `ListAppointmentSlotsUseCase` | date filter params | `List<AppointmentSlotResult>` |
| `CreateAppointmentSlotUseCase` | `CreateSlotCommand` | `AppointmentSlotResult` |
| `CreateBookingUseCase` | `CreateBookingCommand` | `BookingResult` |
| `ListDonorBookingsUseCase` | `UUID accountId` | `List<DonorBookingResult>` |
| `CancelBookingUseCase` | `UUID accountId, UUID bookingId` | `void` |
| `RescheduleBookingUseCase` | `RescheduleBookingCommand` | `DonorBookingResult` |

### Medical (9 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `RecordMedicalCheckUseCase` | `RecordMedicalCheckCommand` | `MedicalCheckResult` |
| `RecordDonationUseCase` | `RecordDonationCommand` | `DonationResult` |
| `PublishDonationUseCase` | `UUID donationId` | `void` |
| `RegisterSampleUseCase` | `RegisterSampleCommand` | `SampleResult` |
| `RegisterAdverseReactionUseCase` | `RegisterAdverseReactionCommand` | `AdverseReactionResult` |
| `UpdateDonorStatusUseCase` | `UpdateDonorStatusCommand` | `void` |
| `ListMedicalQueueUseCase` | none | `List<QueueItemResult>` |
| `ListPendingExaminationsUseCase` | none | `List<ExaminationResult>` |
| `ReviewExaminationUseCase` | `ReviewExaminationCommand` | `void` |

### Nurse (6 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `ListDonationQueueUseCase` | none | `List<DonationQueueResult>` |
| `CreateCollectionSessionUseCase` | `CreateCollectionSessionCommand` | `CollectionSessionResult` |
| `StartCollectionSessionUseCase` | `UUID sessionId` | `CollectionSessionResult` |
| `CompleteCollectionSessionUseCase` | `CompleteCollectionSessionCommand` | `CollectionSessionResult` |
| `AbortCollectionSessionUseCase` | `AbortCollectionSessionCommand` | `CollectionSessionResult` |
| `GetCollectionSessionUseCase` | `UUID sessionId` | `CollectionSessionResult` |

### Lab (6 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `ListPendingLabRequestsUseCase` | none | `List<LabRequestResult>` |
| `ListPendingSamplesUseCase` | none | `List<SampleResult>` |
| `RecordLabResultUseCase` | `RecordLabResultCommand` | `LabResultResult` |
| `PublishLabResultUseCase` | `UUID resultId` | `void` |
| `GetSampleResultsUseCase` | `UUID sampleId` | `List<LabResultResult>` |
| `SubmitLabExaminationUseCase` | `SubmitLabExaminationCommand` | `LabExaminationResult` |

### Admin (13 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `CreateAccountUseCase` | `CreateAccountCommand` | `AccountResult` |
| `UpdateAccountUseCase` | `UpdateAccountCommand` | `AccountResult` |
| `AssignRolesUseCase` | `AssignRolesCommand` | `void` |
| `ListStaffUseCase` | none | `List<StaffResult>` |
| `CreateStaffProfileUseCase` | `CreateStaffProfileCommand` | `StaffResult` |
| `ListDonorsUseCase` | filter params | `List<DonorResult>` |
| `ListEligibleDonorsUseCase` | none | `List<DonorResult>` |
| `RegisterDonorByPhoneUseCase` | `RegisterDonorByPhoneCommand` | `DonorResult` |
| `SendReminderUseCase` | `SendReminderCommand` | `void` |
| `MarkDonorRevisitNotifiedUseCase` | `UUID donorId` | `void` |
| `ListExpiredDocumentsUseCase` | none | `List<ExpiredDocumentResult>` |
| `MarkExpiredDocumentNotifiedUseCase` | `UUID documentId` | `void` |
| `GetReportsSummaryUseCase` | none | `ReportsSummaryResult` |

### Report (7 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `CreateReportRequestUseCase` | `CreateReportRequestCommand` | `ReportRequestResult` |
| `ListMyReportRequestsUseCase` | `UUID accountId` | `List<ReportRequestResult>` |
| `GetReportUseCase` | `UUID requestId` | `ReportResult` |
| `ListAllReportRequestsUseCase` | none | `List<ReportRequestResult>` |
| `TakeReportRequestUseCase` | `UUID requestId, UUID staffId` | `void` |
| `ProcessReportRequestUseCase` | `ProcessReportRequestCommand` | `ReportRequestResult` |
| `RejectReportRequestUseCase` | `RejectReportRequestCommand` | `void` |

### Examination (5 use cases)
| Use Case | Command | Result |
|----------|---------|--------|
| `ListExaminationSlotsUseCase` | date filter params | `List<ExaminationSlotResult>` |
| `CreateExaminationBookingUseCase` | `CreateExaminationBookingCommand` | `ExaminationBookingResult` |
| `GetExaminationBookingUseCase` | `UUID bookingId` | `ExaminationBookingResult` |
| `ConfirmExaminationBookingUseCase` | `UUID bookingId` | `ExaminationBookingResult` |
| `CancelExaminationBookingUseCase` | `UUID bookingId` | `void` |

---

## Изменённые контроллеры

| Контроллер | Кол-во use cases | Статус |
|------------|-----------------|--------|
| `AuthController` | 3 | ✅ Полностью переведён |
| `DonorController` | 7 | ✅ Полностью переведён |
| `AppointmentController` | 6 | ✅ Полностью переведён |
| `MedicalWorkflowController` | 9 | ✅ Полностью переведён |
| `NurseController` | 6 | ✅ Полностью переведён |
| `LabWorkflowController` | 6 | ✅ Полностью переведён |
| `AdminAccountsController` | 6 | ✅ Полностью переведён |
| `AdminController` | 7 | ✅ Полностью переведён |
| `ReportRequestController` | 3 | ✅ Полностью переведён |
| `AdminReportRequestController` | 4 | ✅ Полностью переведён |
| `ExaminationController` | 5 | ✅ Полностью переведён |

---

## Чек-лист ручной проверки (3-5 ключевых эндпоинтов на модуль)

### Auth Module
- [ ] `POST /auth/register` — регистрация нового пользователя
- [ ] `POST /auth/login` — вход в систему
- [ ] `GET /auth/me` — получение профиля текущего пользователя

### Donor Module
- [ ] `GET /donor/profile` — получение профиля донора
- [ ] `PUT /donor/profile` — обновление профиля донора
- [ ] `POST /donor/consent` — подача согласия на донорство
- [ ] `GET /donor/eligibility` — проверка допуска к донации
- [ ] `GET /donor/notifications` — список уведомлений

### Appointment Module
- [ ] `GET /appointments/slots` — список доступных слотов
- [ ] `POST /appointments/bookings` — создание записи
- [ ] `GET /appointments/bookings/my` — мои записи
- [ ] `POST /appointments/bookings/{id}/cancel` — отмена записи
- [ ] `POST /appointments/bookings/{id}/reschedule` — перенос записи

### Medical Module
- [ ] `POST /medical/check` — запись результатов медосмотра
- [ ] `POST /medical/donation` — запись донации
- [ ] `POST /medical/sample` — регистрация образца
- [ ] `GET /medical/queue` — очередь на медосмотр
- [ ] `POST /medical/adverse-reaction` — регистрация нежелательной реакции

### Nurse Module
- [ ] `GET /nurse/queue` — очередь на забор крови
- [ ] `POST /nurse/sessions` — создание сессии забора
- [ ] `POST /nurse/sessions/{id}/start` — начало забора
- [ ] `POST /nurse/sessions/{id}/complete` — завершение забора
- [ ] `POST /nurse/sessions/{id}/abort` — отмена забора

### Lab Module
- [ ] `GET /lab/pending` — ожидающие образцы
- [ ] `POST /lab/results` — запись результата анализа
- [ ] `POST /lab/results/{id}/publish` — публикация результата
- [ ] `GET /lab/samples/{id}/results` — результаты по образцу

### Admin Module
- [ ] `POST /admin/accounts` — создание аккаунта
- [ ] `GET /admin/staff` — список персонала
- [ ] `GET /admin/donors` — список доноров
- [ ] `POST /admin/reminders` — отправка напоминания
- [ ] `GET /admin/reports-summary` — сводка по отчётам

### Report Module
- [ ] `POST /reports` — создание запроса на справку
- [ ] `GET /reports/my` — мои запросы
- [ ] `GET /admin/reports` — все запросы (админ)
- [ ] `POST /admin/reports/{id}/process` — обработка запроса
- [ ] `POST /admin/reports/{id}/reject` — отклонение запроса

### Examination Module
- [ ] `GET /examinations/slots` — доступные слоты обследований
- [ ] `POST /examinations/bookings` — запись на обследование
- [ ] `GET /examinations/bookings/{id}` — детали записи
- [ ] `POST /examinations/bookings/{id}/confirm` — подтверждение
- [ ] `POST /examinations/bookings/{id}/cancel` — отмена

---

## Структура файлов (пример модуля donor)

```
donor/
├── api/
│   ├── DonorController.java          # Зависит от use case интерфейсов
│   └── dto/
│       ├── DonorProfileResponse.java
│       └── ...
├── application/
│   ├── DonorService.java              # Internal helper (delegated to)
│   ├── command/
│   │   ├── UpdateDonorProfileCommand.java
│   │   └── SubmitConsentCommand.java
│   ├── result/
│   │   ├── DonorProfileResult.java
│   │   ├── ConsentResult.java
│   │   └── ...
│   └── usecase/
│       ├── GetDonorProfileUseCase.java        # Interface
│       ├── GetDonorProfileService.java        # Implementation
│       ├── UpdateDonorProfileUseCase.java
│       ├── UpdateDonorProfileService.java
│       └── ...
├── domain/
│   └── DonorProfile.java
└── infra/
    └── jpa/
        └── DonorProfileRepository.java
```

---

## Статус сборки

```
✅ ./gradlew compileJava — BUILD SUCCESSFUL
✅ ./gradlew test — BUILD SUCCESSFUL (все тесты проходят)
```

---

## Итого

- **62 use cases** создано
- **11 контроллеров** обновлено
- **API контракт** — не изменён
- **База данных** — не изменена
- **Все тесты** — проходят
