CREATE TABLE IF NOT EXISTS assets (
  id SERIAL PRIMARY KEY,
  filename VARCHAR,
  content_type VARCHAR,
  size BIGINT,
  url VARCHAR,
  upload_date TIMESTAMPTZ NOT NULL DEFAULT now(),
  status VARCHAR NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING','UPLOADING','COMPLETED','FAILED'))
);

-- Índices básicos (tal como los tienes)
CREATE INDEX IF NOT EXISTS idx_assets_upload_date ON assets (upload_date DESC);
CREATE INDEX IF NOT EXISTS idx_assets_filename     ON assets (filename);
CREATE INDEX IF NOT EXISTS idx_assets_status       ON assets (status);