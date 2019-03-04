CREATE TABLE workflow_param_defaults (
    workflow_id integer NOT NULL,
    key text NOT NULL,
    value text NOT NULL
);

INSERT INTO workflow_param_defaults(workflow_id, key, value) SELECT workflow_id, key, COALESCE(default_value, '') FROM workflow_param;
