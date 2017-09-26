select wr.workflow_run_id, iwr.ius_id, i.sw_accession as ius_swid, lk.id as lims_key_id
from workflow_run wr
right join ius_workflow_runs iwr on wr.workflow_run_id = iwr.workflow_run_id
left join ius i on iwr.ius_id = i.ius_id
left join lims_key lk on i.lims_key_id = lk.lims_key_id