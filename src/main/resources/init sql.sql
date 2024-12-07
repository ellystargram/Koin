use koin;



create table if not exists member(
                       id BIGINT primary key auto_increment,
                       joined_date DATETIME not null
);

create table if not exists wallet(
                       id BIGINT primary key auto_increment,
                       address TEXT not null,
                       user_id BIGINT not null,
                       foreign key (user_id) references member(id),
                       wallet_type TEXT not null,
                       created_date DATETIME not null
);

create table if not exists pronounce(
                          id BIGINT primary key auto_increment,
                          user_id BIGINT,
                          foreign key (user_id) references member(id),
                          pronounce TEXT not null
);

insert into pronounce(user_id, pronounce) values(null, '코인아');
insert into pronounce(user_id, pronounce) values(null, 'hey coin');
insert into pronounce(user_id, pronounce) values(null, '헤이 코인');
insert into pronounce(user_id, pronounce) values(null, '헤이코인');
insert into pronounce(user_id, pronounce) values(null, 'hey koin');
insert into pronounce(user_id, pronounce) values(null, '코인');
insert into pronounce(user_id, pronounce) values(null, 'koin');
insert into pronounce(user_id, pronounce) values(null, 'coin');
insert into pronounce(user_id, pronounce) values(null, 'heycoin');
insert into pronounce(user_id, pronounce) values(null, 'heykoin');

# select *
# from pronounce;

drop table if exists command;

create table if not exists command(
    id BIGINT primary key auto_increment,
    keyword TEXT not null,
    operate_as TEXT not null
);

# 가입 명령어들
insert into command(keyword, operate_as) values('가입', 'join');
insert into command(keyword, operate_as) values('join', 'join');
insert into command(keyword, operate_as) values('회원가입', 'join');

# 탈퇴 명령어들
insert into command(keyword, operate_as) values('탈퇴', 'leave');
insert into command(keyword, operate_as) values('leave', 'leave');
insert into command(keyword, operate_as) values('회원탈퇴', 'leave');

# 지갑 생성 명령어들
insert into command(keyword, operate_as) values('지갑생성', 'create_wallet');
insert into command(keyword, operate_as) values('create_wallet', 'create_wallet');
insert into command(keyword, operate_as) values('지갑 생성', 'create_wallet');
insert into command(keyword, operate_as) values('create wallet', 'create_wallet');

# 지갑 삭제 명령어들
insert into command(keyword, operate_as) values('지갑삭제', 'delete_wallet');
insert into command(keyword, operate_as) values('delete_wallet', 'delete_wallet');
insert into command(keyword, operate_as) values('지갑 삭제', 'delete_wallet');
insert into command(keyword, operate_as) values('delete wallet', 'delete_wallet');

# 지갑 조회 명령어들
insert into command(keyword, operate_as) values('지갑조회', 'get_wallet');
insert into command(keyword, operate_as) values('get_wallet', 'get_wallet');
insert into command(keyword, operate_as) values('지갑 조회', 'get_wallet');
insert into command(keyword, operate_as) values('get wallet', 'get_wallet');

create table if not exists crypto_exchanger(
    id BIGINT primary key auto_increment,
    name TEXT not null,
    default_fee_rate DECIMAL(20, 10) not null
);

insert into crypto_exchanger(name, default_fee_rate) values ('코인원', 0.002);
insert into crypto_exchanger(name, default_fee_rate) values ('업비트', 0.0025);
insert into crypto_exchanger(name, default_fee_rate) values ('빗썸', 0.0025);

create table if not exists crypto_currency(
    id BIGINT primary key auto_increment,
    name TEXT not null,
    symbol TEXT not null,
    simple_name TEXT not null,
    price_usd DECIMAL(32, 2) not null,
    exchange_fee_boost_rate DECIMAL(20, 10) not null default 1.0,
    max_decimal_point INT not null,
    max_transaction_amount DECIMAL(32, 10) not null default 1000000000,
    max_transaction_amount_per_day DECIMAL(32, 10) not null default 1000000000,
    max_transaction_match_time INT not null default 600
);

insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Bitcoin', '₿', 'BTC', 96256.63, 8);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Ethereum', 'Ξ', 'ETH', 3560.50, 18);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Solana', 'S◎', 'SOL', 238.71, 2);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Cardano', '₳', 'ADA', 1.06, 0);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('DOGE', 'Ð', 'DOGE', 0.41, 0);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Polkadot', '◎', 'DOT', 8.643, 0);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Uniswap', 'U', 'UNI', 13.35, 0);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Chainlink', '⛓', 'LINK', 25.45, 0);
insert into crypto_currency(name, symbol, simple_name, price_usd, max_decimal_point) values ('Luna', 'L◎', 'LUNA', 0.00000001, 2); # 이스터에그임

select * from crypto_currency;

create table if not exists tradable_crypto(
    id BIGINT primary key auto_increment,
    from_currency_id BIGINT not null,
    foreign key (from_currency_id) references crypto_currency(id),
    to_currency_id BIGINT not null,
    foreign key (to_currency_id) references crypto_currency(id),
    exchanger_id BIGINT not null,
    foreign key (exchanger_id) references crypto_exchanger(id),
    fee_rate DECIMAL(20, 10) not null
);

create table if not exists wallet(
    id BIGINT primary key auto_increment,
    address TEXT not null,
    user_id BIGINT not null,
    foreign key (user_id) references member(id),
    exchanger_id BIGINT not null,
    foreign key (exchanger_id) references crypto_exchanger(id),
    created_date DATETIME not null
);

select * from wallet;