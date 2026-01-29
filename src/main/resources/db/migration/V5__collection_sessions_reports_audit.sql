-- Collection sessions for nurse workflow
CREATE TABLE collection_session (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_id uuid NOT NULL UNIQUE REFERENCES visit(id) ON DELETE CASCADE,
    nurse_staff_id uuid REFERENCES staff_profile(id) ON DELETE SET NULL,
    status text NOT NULL DEFAULT 'PREPARED',
    started_at timestamptz,
    ended_at timestamptz,
    pre_vitals_json jsonb,
    post_vitals_json jsonb,
    notes text,
    complications text,
    interruption_reason text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_collection_session_visit ON collection_session (visit_id);
CREATE INDEX idx_collection_session_status ON collection_session (status);

-- Report request workflow
CREATE TABLE report_request (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    donor_id uuid NOT NULL REFERENCES donor_profile(id) ON DELETE CASCADE,
    requested_by_staff_id uuid NOT NULL REFERENCES staff_profile(id) ON DELETE RESTRICT,
    requested_by_role text,
    assigned_admin_id uuid REFERENCES staff_profile(id) ON DELETE SET NULL,
    report_type text NOT NULL,
    status text NOT NULL DEFAULT 'REQUESTED',
    payload_json jsonb,
    generated_at timestamptz,
    message text,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_report_request_status ON report_request (status);
CREATE INDEX idx_report_request_donor ON report_request (donor_id);
CREATE INDEX idx_report_request_requested_by ON report_request (requested_by_staff_id);

-- Audit log
CREATE TABLE audit_event (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id uuid REFERENCES account(id) ON DELETE SET NULL,
    action text NOT NULL,
    entity_type text NOT NULL,
    entity_id uuid,
    created_at timestamptz NOT NULL DEFAULT now(),
    metadata_json jsonb
);

CREATE INDEX idx_audit_event_account ON audit_event (account_id);
CREATE INDEX idx_audit_event_entity ON audit_event (entity_type, entity_id);
