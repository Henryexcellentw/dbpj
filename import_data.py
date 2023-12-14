import mysql.connector
from faker import Faker
from datetime import datetime, timedelta
import random

def insert_users(faker, db_config, n):
    # 建立数据库连接
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    for i in range(n):
        name = faker.user_name()
        if len(name) > 10:
            name = name[:10]
        password = faker.password()
        age = faker.random_int(min=18, max=80)
        gender = faker.random_element(elements=('Male', 'Female'))
        role = faker.random_element(elements=(0, 1))
        phone_number = faker.random_int(min=10000000000, max=19999999999)
        sql = "INSERT INTO `user` (`name`, `age`, `phoneNumber`, `password`, `role`, `gender`) VALUES (%s, %s, %s, %s, %s, %s)"
        values = (name, age, phone_number, password, role, gender)
        try:
            cursor.execute(sql, values)
        except mysql.connector.errors.IntegrityError as err:
            print("user insert error! ", err)

    conn.commit()
    cursor.close()
    conn.close()


def insert_platforms(db_config):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    platform_names = [
    "蓝天商城",
    "瑞星优品",
    "翔云购物",
    "金谷物资",
    "红叶贸易",
    "蓬莱百货",
    "明珠购",
    "翼飞国际",
    "维纳斯商城",
    "尚品坊",
    "紫禁之都",
    "巴比伦市场",
    "星辰大地",
    "丰盛购物中心",
    "太阳之门"
    ]   

    for platform in platform_names:
        sql = "INSERT INTO `platform` (`name`) VALUES (%s)"
        values = (platform,)
        try:
            cursor.execute(sql, values)
        except mysql.connector.errors.IntegrityError as err:
            print("Platform name already exists! ", err)

    conn.commit()
    cursor.close()
    conn.close()


def insert_shop(faker, db_config):
    def get_owner_ids(cursor):
        query = "SELECT id FROM `user` WHERE `role` = 1"
        cursor.execute(query)
        owner_ids = [user[0] for user in cursor.fetchall()]
        return owner_ids

    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    owner_ids = get_owner_ids(cursor);
    for owner_id in owner_ids:
        merchant_name = faker.company()
        address = faker.address()

        # 构建插入数据的 SQL 语句
        sql = "INSERT INTO `shop` (`name`, `address`, `owner_id`) VALUES (%s, %s, %s)"
        values = (merchant_name, address, owner_id)
        try:
            cursor.execute(sql, values)
        except mysql.connector.errors.IntegrityError as err:
            print(err)
        
    conn.commit()
    cursor.close()
    conn.close()


def insert_commodities(faker, n):
    def get_shop_ids(cursor):
        query = "SELECT id FROM `shop`"
        cursor.execute(query)
        ids = [item[0] for item in cursor.fetchall()]
        return ids
    
    def get_platform_ids(cursor):
        query = "SELECT id FROM `platform`"
        cursor.execute(query)
        ids = [item[0] for item in cursor.fetchall()]
        return ids
    
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    shop_ids = get_shop_ids(cursor)
    platform_ids = get_platform_ids(cursor)
    commodity_names = []
    for i in range(50):
        prod_name = faker.word()
        if prod_name not in commodity_names:
            commodity_names.append(prod_name)
    categories = [
    "电子产品",
    "时尚服饰",
    "家居装饰",
    "食品与饮料",
    "运动健身",
    "图书与音像",
    "美妆护肤",
    "宠物用品",
    "玩具与游戏",
    "汽车配件"
    ]

    for i in range(n):
        commodity_name = faker.random_element(elements=commodity_names)
        category = faker.random_element(elements=categories)
        origin = faker.city()
        produce_date = faker.date_between(start_date='-1y', end_date='today')
        price = round(faker.random.uniform(10, 500), 2)
        shop_id = faker.random_element(elements=shop_ids)
        platform_id = faker.random_element(elements=platform_ids)
        description = faker.sentence(nb_words=20)

        sql = """
        INSERT INTO `commodity` (`name`, `category`, `description`, `produceDate`, `origin`, `s_id`, `p_id`) 
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        values = (commodity_name, category, description, produce_date, origin, shop_id, platform_id)
        try:
            cursor.execute(sql, values)
        except mysql.connector.errors.IntegrityError as err:
            print(err)

    conn.commit()
    cursor.close()
    conn.close()

def insert_price(faker):
    def get_commodity_ids(cursor):
        query = "SELECT id FROM `commodity`"
        cursor.execute(query)
        ids = [item[0] for item in cursor.fetchall()]
        return ids
    
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    commodity_ids = get_commodity_ids(cursor)
    print(len(commodity_ids))

    for commodity_id in commodity_ids:
        time_set = set()
        for i in range(5):
            time = faker.date_between(start_date='-1y', end_date='today')
            while(time in time_set):
                time = faker.date_between(start_date='-1y', end_date='today')
            time_set.add(time)
            price = round(faker.random.uniform(10, 500), 2)

            # 构建插入数据的 SQL 语句
            sql = "INSERT INTO `price` (`c_id`, `time`, `price`) VALUES (%s, %s, %s)"
            values = (commodity_id, time, price)
            try:
                cursor.execute(sql, values)
            except mysql.connector.errors.IntegrityError as err:
                print(err)

    conn.commit()
    cursor.close()
    conn.close()


def insert_message(faker, n):
    def get_user_collection_info(cursor):
        query = "SELECT u_id, c_id FROM `collection`"
        cursor.execute(query)
        result_dict = {}

        for u_id, c_id in cursor.fetchall():
            if u_id in result_dict:
                result_dict[u_id].append(c_id)
            else:
                result_dict[u_id] = [c_id]
        return result_dict
    
    def get_sql_datetime(faker):
        random_datetime = faker.date_time_this_decade()
        sql_datetime = random_datetime.strftime("%Y-%m-%d %H:%M:%S")
        return sql_datetime
    
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    user_collection_info = get_user_collection_info(cursor)

    for i in range(n):
        user_id = faker.random_element(elements=list(user_collection_info.keys()))

        collection_list = user_collection_info[user_id]
        commodity_id = faker.random_element(elements=collection_list)

        time = get_sql_datetime(faker)
        content = "您收藏的商品 id 为： " + str(commodity_id) + " 的商品降价了！"

        # 构建插入数据的 SQL 语句
        sql = "INSERT INTO `message` (`u_id`, `time`, `content`) VALUES (%s, %s, %s)"
        values = (user_id, time, content)
        try:
            cursor.execute(sql, values)
        except mysql.connector.errors.IntegrityError as err:
            print(err)

    conn.commit()
    cursor.close()
    conn.close()


def insert_collection(faker, n):
    def get_user_ids(cursor):
        query = "SELECT id FROM `user` where `role` = 0"
        cursor.execute(query)
        ids = [item[0] for item in cursor.fetchall()]
        return ids
    
    def get_commodity_ids(cursor):
        query = "SELECT id FROM `commodity`"
        cursor.execute(query)
        ids = [item[0] for item in cursor.fetchall()]
        return ids
    
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()
    user_ids = get_user_ids(cursor)
    commodity_ids = get_commodity_ids(cursor)

    for i in range(n):
        user_id = faker.random_element(elements=user_ids)
        commodity_id = faker.random_element(elements=commodity_ids)
        floor_price = faker.random_int(min=10, max=500)
        sql = "INSERT INTO `collection` (`u_id`, `c_id`, `floorprice`) VALUES (%s, %s, %s)"
        values = (user_id, commodity_id, floor_price)
        try:
            cursor.execute(sql, values)
        except mysql.connector.errors.IntegrityError as err:
            print(err)

    conn.commit()
    cursor.close()
    conn.close()

if __name__ == '__main__':
    db_config = {
        'host': 'localhost',
        'user': 'root',
        'password': 'root',
        'database': 'databasepj',
    }
    # insert_users(Faker('en_US'), db_config, 500)
    # insert_platforms(db_config)
    # insert_shop(Faker('zh_CN'), db_config)
    # insert_commodities(Faker('zh_CN'), 1000)
    insert_price(Faker('zh_CN'))
    # insert_collection(Faker('zh_CN'), 500)
    # insert_message(Faker('zh_CN'), 500)