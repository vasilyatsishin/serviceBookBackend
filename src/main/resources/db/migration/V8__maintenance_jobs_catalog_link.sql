ALTER TABLE maintenance_jobs
    ADD COLUMN catalog_id INT,
    ADD CONSTRAINT fk_mj_catalog FOREIGN KEY (catalog_id) REFERENCES services_catalog (id) ON DELETE SET NULL;
