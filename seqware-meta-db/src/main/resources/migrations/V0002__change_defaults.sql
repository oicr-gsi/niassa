INSERT INTO workflow_param_defaults(workflow_id, key, value) SELECT workflow_id, key, default_value FROM workflow_param;
