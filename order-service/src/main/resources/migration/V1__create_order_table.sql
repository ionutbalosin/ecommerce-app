-- 'ossp' is not loaded by default, hence the extension must be explicitly enabled in case it does not exist
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS ORDER (
  ORDER_ID SERIAL PRIMARY KEY,
  ID UUID DEFAULT uuid_generate_v4() NOT NULL,
  SOURCE_EVENT_ID UUID NOT NULL,
  USER_ID UUID NOT NULL,
  AMOUNT REAL NOT NULL,
  CURRENCY VARCHAR(3) NOT NULL CHECK (CURRENCY in ('EUR')),
  DETAILS JSONB NOT NULL,
  STATUS VARCHAR(2) NOT NULL CHECK (STATUS in ('PI', 'PA', 'PF', 'S', 'CM', 'CN')),
  DAT_INS TIMESTAMP NOT NULL,
  DAT_UPD TIMESTAMP,
  USR_INS VARCHAR(256) NOT NULL,
  USR_UPD VARCHAR(256),
  STAT CHAR(1) NOT NULL CHECK (STAT in ('A', 'I', 'D'))
);

COMMENT ON COLUMN ORDER.ORDER_ID IS 'Auto-generated internal id (i.e. primary key)';
COMMENT ON COLUMN ORDER.ID IS 'Order id used as an external identifier';
COMMENT ON COLUMN ORDER.SOURCE_EVENT_ID IS 'Source (i.e., originator) event id';
COMMENT ON COLUMN ORDER.USER_ID IS 'Identity of the user that created the order';
COMMENT ON COLUMN ORDER.AMOUNT IS 'Order amount';
COMMENT ON COLUMN ORDER.CURRENCY IS 'Order currency';
COMMENT ON COLUMN ORDER.DETAILS IS 'Order details (including the list of products)';
COMMENT ON COLUMN ORDER.STATUS IS 'Order status PI = payment initiated, PA = payment approved, PF = payment failed, S = shipping, CM = completed, CN = cancelled';
COMMENT ON COLUMN ORDER.DAT_INS IS 'Creation date of the record';
COMMENT ON COLUMN ORDER.DAT_UPD IS 'Modification date of the record';
COMMENT ON COLUMN ORDER.USR_INS IS 'Identity of the user that created the record';
COMMENT ON COLUMN ORDER.USR_UPD IS 'Identity of the user that modified the record';
COMMENT ON COLUMN ORDER.STAT IS 'Status of the record A = active, I = inactive, D = deleted';

CREATE INDEX ORDER_IDX1 ON ORDER (ID);
CREATE INDEX ORDER_IDX2 ON ORDER (USER_ID);
CREATE UNIQUE INDEX ORDER_UIDX1 ON ORDER (SOURCE_EVENT_ID, USER_ID);