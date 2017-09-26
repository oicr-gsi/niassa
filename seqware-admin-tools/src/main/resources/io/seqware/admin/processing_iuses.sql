select wr.workflow_run_id, pi.ius_id, i.sw_accession as ius_swid, p.processing_id
from workflow_run wr
left join processing p on (wr.workflow_run_id = p.workflow_run_id OR wr.workflow_run_id = p.ancestor_workflow_run_id)
left join processing_ius pi on (p.processing_id = pi.processing_id)
left join ius i on (pi.ius_id = i.ius_id)
where pi.ius_id is not null