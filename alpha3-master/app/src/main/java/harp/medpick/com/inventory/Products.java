package harp.medpick.com.inventory;

/***/

public class Products {


    public void Products(){

    }

    String name, barcode, price, buydate, supplier, warrantydate;

    public Products(String name, String barcode, String price, String buydate, String supplier, String warrantydate) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.buydate = buydate;
        this.supplier = supplier;
        this.warrantydate = warrantydate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() { return barcode;    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBuydate() {
        return buydate;
    }

    public void setBuydate(String buydate) { this.buydate = buydate;    }

    public String getSupplier() { return supplier;    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;    }

    public String getWarrantydate() {
        return warrantydate;
    }

    public void setWarrantydate(String warrantydate) {
        this.warrantydate = warrantydate;
    }

}

