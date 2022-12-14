create table order_details
(
    id serial primary key,
    order_id int,
    product_detail_id int,
    price_sell float,
    product_price float,
    amount int,
    ctime timestamp DEFAULT current_timestamp ,
    mtime timestamp DEFAULT NULL,
    note text,
    constraint fk_orders FOREIGN KEY (order_id) REFERENCES orders(id),
    constraint fk_product_details FOREIGN KEY (product_detail_id) REFERENCES product_details(id)
);