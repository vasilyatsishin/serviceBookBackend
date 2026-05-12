ALTER TABLE performed_maintenance
    MODIFY COLUMN maintenance_job_id INT NULL,
    ADD COLUMN catalog_id INT,
    ADD CONSTRAINT fk_pm_catalog FOREIGN KEY (catalog_id) REFERENCES services_catalog (id) ON DELETE SET NULL;
