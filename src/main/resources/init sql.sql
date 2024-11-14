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

create table if not exists coin(
                     id BIGINT primary key auto_increment,
                     coin_name TEXT not null,
                     coin_symbol TEXT not null,
                     coin_simple_name TEXT not null,
                     price_usd DECIMAL(20, 10) not null,
                     transaction_amount DECIMAL(20, 10) not null
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
insert into pronounce(user_id, pronounce) values(null, 'hey koin');
insert into pronounce(user_id, pronounce) values(null, '코인');
insert into pronounce(user_id, pronounce) values(null, 'koin');

# select *
# from pronounce;

create table command(
    id BIGINT primary key auto_increment,
    keyword TEXT not null,
    operate_as TEXT not null
);

# 가입 명령어들
insert into command(keyword, operate_as) values('가입', 'join');
insert into command(keyword, operate_as) values('join', 'join');
insert into command(keyword, operate_as) values('회원가입', 'join');