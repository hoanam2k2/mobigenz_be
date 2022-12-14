
CREATE OR REPLACE FUNCTION updateStockAfterDeleteImei()
    RETURNS trigger AS
$$
BEGIN
    UPDATE PRODUCT_DETAILS product
    SET STOCK = (SELECT COUNT(IMEI.ID) FROM IMEI WHERE IMEI.STATUS = 1 AND IMEI.PRODUCT_DETAIL_ID =  OLD.product_detail_id  )
    WHERE product.ID = OLD.product_detail_id;
    RETURN NEW;
END;
$$
    LANGUAGE 'plpgsql';
CREATE TRIGGER UPDATE_STOCK_AFTER_DELETE_IMEI
    AFTER DELETE
    ON IMEI
    FOR EACH ROW
EXECUTE FUNCTION updateStockAfterDeleteImei();