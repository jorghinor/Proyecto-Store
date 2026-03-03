ALTER TABLE category ADD COLUMN deleted boolean DEFAULT false;

UPDATE category SET deleted = false;