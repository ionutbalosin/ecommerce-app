CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS ORDERS (
  ORDER_ID SERIAL PRIMARY KEY,
  ID UUID DEFAULT uuid_generate_v4() NOT NULL,
  SOURCE_EVENT_ID UUID NOT NULL,
  USER_ID UUID NOT NULL,
  AMOUNT NUMERIC(12,2) NULL DEFAULT 0,
  CURRENCY VARCHAR(3) NOT NULL CHECK (CURRENCY in ('EUR')),
  DETAILS JSONB NOT NULL,
  STATUS VARCHAR(2) NOT NULL CHECK (STATUS in ('NW', 'PA', 'PF', 'SP', 'SC', 'SF', 'CM', 'CN')),
  DAT_INS TIMESTAMP NOT NULL,
  DAT_UPD TIMESTAMP,
  USR_INS VARCHAR(256) NOT NULL,
  USR_UPD VARCHAR(256),
  STAT CHAR(1) NOT NULL CHECK (STAT in ('A', 'I', 'D'))
);

COMMENT ON COLUMN ORDERS.ORDER_ID IS 'Auto-generated internal id (i.e. primary key)';
COMMENT ON COLUMN ORDERS.ID IS 'Order id used as an external identifier';
COMMENT ON COLUMN ORDERS.SOURCE_EVENT_ID IS 'Source (i.e., originator) event id';
COMMENT ON COLUMN ORDERS.USER_ID IS 'Identity of the user that created the order';
COMMENT ON COLUMN ORDERS.AMOUNT IS 'Order amount';
COMMENT ON COLUMN ORDERS.CURRENCY IS 'Order currency';
COMMENT ON COLUMN ORDERS.DETAILS IS 'Order details (including the list of products)';
COMMENT ON COLUMN ORDERS.STATUS IS 'Order status NW = new, PA = payment approved, PF = payment failed, SP = shipping in progress, SC = shipping completed, SF = shipping failed, CM = completed, CN = cancelled';
COMMENT ON COLUMN ORDERS.DAT_INS IS 'Creation date of the record';
COMMENT ON COLUMN ORDERS.DAT_UPD IS 'Modification date of the record';
COMMENT ON COLUMN ORDERS.USR_INS IS 'Identity of the user that created the record';
COMMENT ON COLUMN ORDERS.USR_UPD IS 'Identity of the user that modified the record';
COMMENT ON COLUMN ORDERS.STAT IS 'Status of the record A = active, I = inactive, D = deleted';

CREATE INDEX ORDER_IDX1 ON ORDERS (ID);
CREATE INDEX ORDER_IDX2 ON ORDERS (USER_ID);
CREATE UNIQUE INDEX ORDER_UIDX1 ON ORDERS (SOURCE_EVENT_ID, USER_ID);