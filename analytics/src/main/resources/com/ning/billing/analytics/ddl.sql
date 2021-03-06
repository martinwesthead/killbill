drop table if exists bst;
create table bst (
  event_key varchar(50) not null
, requested_timestamp bigint not null
, event varchar(50) not null
, prev_product_name varchar(32) default null
, prev_product_type varchar(32) default null
, prev_product_category varchar(32) default null
, prev_slug varchar(50) default null
, prev_phase varchar(32) default null
, prev_price numeric(10, 4) default 0
, prev_mrr numeric(10, 4) default 0
, prev_currency varchar(32) default null
, prev_start_date bigint default null
, prev_state varchar(32) default null
, prev_subscription_id varchar(100) default null
, prev_bundle_id varchar(100) default null
, next_product_name varchar(32) default null
, next_product_type varchar(32) default null
, next_product_category varchar(32) default null
, next_slug varchar(50) default null
, next_phase varchar(32) default null
, next_price numeric(10, 4) default 0
, next_mrr numeric(10, 4) default 0
, next_currency varchar(32) default null
, next_start_date bigint default null
, next_state varchar(32) default null
, next_subscription_id varchar(100) default null
, next_bundle_id varchar(100) default null
) engine=innodb;

create index bst_key_index on bst (event_key, requested_timestamp asc);
