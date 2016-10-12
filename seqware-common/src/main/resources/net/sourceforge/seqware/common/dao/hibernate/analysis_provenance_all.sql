 SELECT coalesce(pius.lims_ids, wrius.lims_ids)             AS "iusLimsKeys",
       coalesce(pius.ius_attributes, wrius.ius_attributes) AS "iusAttributes",
       --       greatest(wr.update_tstmp, pff.update_tstmp, w.update_tstmp) AS "lastModified",
       pff.update_tstmp                                    AS "lastModified",-- rename to createdTstmp
       w.name                                              AS "workflowName",
       w.version                                           AS "workflowVersion",
       w.sw_accession                                      AS "workflowId",
       w_attrs.attrs                                       AS "workflowAttributes",
       wr.name                                             AS "workflowRunName",
       wr.status                                           AS "workflowRunStatus",
       wr.sw_accession                                     AS "workflowRunId",
       wr_attrs.attrs                                      AS "workflowRunAttributes",
       wrifs.swids                                         AS "workflowRunInputFileIds",
       pff.processing_algorithm                            AS "processingAlgorithm",
       pff.processing_swid                                 AS "processingId",
       pff.processing_status                               AS "processingStatus",
       pff.processing_attrs                                AS "processingAttributes",
       pff.file_meta_type                                  AS "fileMetaType",
       pff.file_swid                                       AS "fileId",
       pff.file_path                                       AS "filePath",
       pff.file_md5sum                                     AS "fileMd5sum",
       pff.file_size                                       AS "fileSize",
       pff.file_description                                AS "fileDescription",
       pff.file_attrs                                      AS "fileAttributes",
       coalesce(pius.skip, wrius.skip)                     AS "skip"
FROM   (SELECT CASE
                 WHEN array_agg(i.sw_accession) = '{NULL}' THEN NULL
                 ELSE array_to_string(array_agg(i.sw_accession
                                                || ','
                                                || lk.provider
                                                || ','
                                                || lk.id
                                                || ','
                                                || lk.version
                                                || ','
                                                || lk.last_modified), ';')
               END                                          lims_ids,
               array_to_string(array_agg(ia.tag
                                         || '='
                                         || ia.value), ';') ius_attributes,
               iwr.workflow_run_id,
               bool_or(i.skip)                              AS skip
        FROM   ius i
               right outer join lims_key lk
                             ON i.lims_key_id = lk.lims_key_id
               left join ius_attribute ia
                      ON i.ius_id = ia.ius_id
               left join ius_workflow_runs iwr
                      ON i.ius_id = iwr.ius_id
        GROUP  BY iwr.workflow_run_id,
                  CASE
                    WHEN iwr.workflow_run_id IS NULL THEN i.ius_id
                    ELSE 0
                  END) AS wrius
       left join workflow_run wr
              ON wr.workflow_run_id = wrius.workflow_run_id
       left join workflow w
              ON wr.workflow_id = w.workflow_id
       left join (SELECT wr.workflow_run_id                                      workflow_run_id,
                         w.workflow_id                                           workflow_id,
                         p.update_tstmp,
                         p.algorithm                                             processing_algorithm,
                         p.sw_accession                                          processing_swid,
                         p.processing_id                                         processing_id,
                         p.status                                                processing_status,
                         (SELECT array_to_string(array_agg(tag
                                                           || '='
                                                           || value), ';')
                          FROM   processing_attribute
                          WHERE  p.processing_id = processing_id
                          GROUP  BY processing_id)                               AS processing_attrs,
                         f.meta_type                                             file_meta_type,
                         f.sw_accession                                          file_swid,
                         f.file_path                                             file_path,
                         f.md5sum                                                file_md5sum,
                         f.SIZE                                                  file_size,
                         f.description                                           file_description,
                         (SELECT array_to_string(array_agg(tag
                                                           || '='
                                                           || value), ';')
                          FROM   file_attribute
                          WHERE  f.file_id = file_id
                          GROUP  BY file_id)                                     file_attrs
                  FROM   processing p
                         right outer join processing_files pf
                                       ON ( p.processing_id = pf.processing_id )
                         right outer join FILE f
                                       ON ( pf.file_id = f.file_id )
                         left join workflow_run wr
                                ON ( p.workflow_run_id = wr.workflow_run_id
                                      OR p.ancestor_workflow_run_id = wr.workflow_run_id )
                         left join workflow w
                                ON ( wr.workflow_id = w.workflow_id )) AS pff
              ON pff.workflow_run_id = wr.workflow_run_id
       left join (SELECT pi.processing_id,
                         CASE
                           WHEN array_agg(i.sw_accession) = '{NULL}' THEN NULL
                           ELSE array_to_string(array_agg(i.sw_accession
                                                          || ','
                                                          || lk.provider
                                                          || ','
                                                          || lk.id
                                                          || ','
                                                          || lk.version
                                                          || ','
                                                          || lk.last_modified), ';')
                         END                                          lims_ids,
                         array_to_string(array_agg(ia.tag
                                                   || '='
                                                   || ia.value), ';') ius_attributes,
                         bool_or(i.skip)                              AS skip
                  FROM   processing_ius pi
                         left join ius i
                                ON pi.ius_id = i.ius_id
                         left join lims_key lk
                                ON i.lims_key_id = lk.lims_key_id
                         left join ius_attribute ia
                                ON i.ius_id = ia.ius_id
                  GROUP  BY pi.processing_id) AS pius
              ON pff.processing_id = pius.processing_id
       left join (SELECT wrif.workflow_run_id,
                         array_to_string(array_agg(f.sw_accession), ',') swids
                  FROM   workflow_run_input_files wrif
                         left join FILE f
                                ON wrif.file_id = f.file_id
                  GROUP  BY wrif.workflow_run_id) as wrifs
              ON wr.workflow_run_id = wrifs.workflow_run_id
       left join (SELECT workflow_run_id,
                         array_to_string(array_agg(tag
                                                   || '='
                                                   || value), ';') attrs
                  FROM   workflow_run_attribute
                  GROUP  BY workflow_run_id) wr_attrs
              on wrius.workflow_run_id = wr_attrs.workflow_run_id
       left join (SELECT workflow_id,
                         array_to_string(array_agg(tag
                                                   || '='
                                                   || value), ';') attrs
                  FROM   workflow_attribute
                  GROUP  BY workflow_id) w_attrs
              ON w.workflow_id = w_attrs.workflow_id  