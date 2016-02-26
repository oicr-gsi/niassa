--update timestamp function
--CREATE OR REPLACE FUNCTION update_tstmp_column()	
--RETURNS TRIGGER AS $$
--BEGIN
--    NEW.update_tstmp = now();
--    RETURN NEW;	
--END;
--$$ language 'plpgsql';

--create MetadataObjectKey table
CREATE TABLE lims_key (
    lims_key_id integer PRIMARY KEY,
    provider text NOT NULL,
    id text NOT NULL,
    version text NOT NULL,
    last_modified timestamp with time zone NOT NULL,
    sw_accession integer DEFAULT nextval('sw_accession_seq'::regclass),
    create_tstmp timestamp without time zone NOT NULL,
    update_tstmp timestamp without time zone NOT NULL
);
CREATE SEQUENCE lims_key_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;
-------------ALTER TABLE public.lims_key_id_seq OWNER TO seqware;
ALTER SEQUENCE lims_key_id_seq OWNED BY lims_key.lims_key_id;
ALTER TABLE lims_key ALTER COLUMN lims_key_id SET DEFAULT nextval('lims_key_id_seq '::regclass);
-------------ALTER TABLE public.lims_key OWNER TO seqware;
--CREATE TRIGGER update_lims_key_update_tstmp BEFORE UPDATE ON lims_key FOR EACH ROW EXECUTE PROCEDURE update_tstmp_column();

--modify IUS table
ALTER TABLE public.ius ALTER COLUMN sample_id DROP NOT NULL;
ALTER TABLE public.ius ALTER COLUMN lane_id DROP NOT NULL;
ALTER TABLE public.ius ADD COLUMN lims_key_id integer;
ALTER TABLE public.ius ADD CONSTRAINT ius__lims_key_fk FOREIGN KEY (lims_key_id) REFERENCES lims_key(lims_key_id);
