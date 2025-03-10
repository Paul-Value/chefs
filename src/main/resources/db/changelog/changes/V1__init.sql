-- Таблица пользователей (user)
CREATE TABLE "user" (
                        user_id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) UNIQUE,
                        address_id BIGINT
);

-- Таблица адресов (address)
CREATE TABLE address (
                         address_id BIGSERIAL PRIMARY KEY,
                         longitude NUMERIC(12, 9) NOT NULL,
                         latitude NUMERIC(12, 9) NOT NULL,
                         formatted_address TEXT NOT NULL,
                         entrance VARCHAR(20),
                         floor VARCHAR(20),
                         apartment VARCHAR(20)
);

-- Внешний ключ для address_id в user
ALTER TABLE "user"
    ADD CONSTRAINT fk_user_address
        FOREIGN KEY (address_id) REFERENCES address(address_id);

-- Таблица поваров (chef)
CREATE TABLE chef (
                      chef_id BIGINT PRIMARY KEY REFERENCES "user"(user_id),
                      photo_url VARCHAR(512),
                      description TEXT,
                      phone VARCHAR(20) NOT NULL,
                      access_code VARCHAR(100) UNIQUE NOT NULL,
                      is_working BOOLEAN DEFAULT false
);

-- Таблица заказчиков (customer)
CREATE TABLE customer (
                          customer_id BIGINT PRIMARY KEY REFERENCES "user"(user_id),
                          device_id VARCHAR(255) UNIQUE NOT NULL,
                          phone VARCHAR(20) NOT NULL
);

-- Таблица ингредиентов (ingredient)
CREATE TABLE ingredient (
                            ingredient_id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) UNIQUE NOT NULL
);

-- Таблица блюд (food)
CREATE TABLE food (
                      food_id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      description TEXT,
                      photo_url VARCHAR(512),
                      price NUMERIC(10, 2) NOT NULL CHECK (price > 0),
                      chef_id BIGINT NOT NULL REFERENCES chef(chef_id)
);

-- Таблица связи многие-ко-многим (food_ingredient)
CREATE TABLE food_ingredient (
                                 food_id BIGINT REFERENCES food(food_id) ON DELETE CASCADE,
                                 ingredient_id BIGINT REFERENCES ingredient(ingredient_id) ON DELETE CASCADE,
                                 PRIMARY KEY (food_id, ingredient_id)
);

-- Таблица заказов (order)
CREATE TABLE "order" (
                         order_id BIGSERIAL PRIMARY KEY,
                         chef_id BIGINT NOT NULL REFERENCES chef(chef_id),
                         customer_id BIGINT NOT NULL REFERENCES customer(customer_id),
                         comment TEXT,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         status VARCHAR(20) NOT NULL CHECK (
                             status IN ('created', 'in_cooking', 'delivering', 'completed', 'canceled')
                             ),
                         total_price NUMERIC(10, 2) NOT NULL CHECK (total_price >= 0),
                         items_json JSONB NOT NULL
);

-- Индексы для оптимизации
CREATE INDEX idx_chef_is_working ON chef(is_working);
CREATE INDEX idx_order_status ON "order"(status);
CREATE INDEX idx_address_coords ON address(latitude, longitude);
CREATE INDEX idx_food_chef ON food(chef_id);

-- Комментарии к таблицам
COMMENT ON TABLE "user" IS 'Основная таблица пользователей';
COMMENT ON COLUMN "order".items_json IS 'JSON вида [{food_id: 1, quantity: 2}]';
COMMENT ON COLUMN chef.access_code IS 'Уникальный код для авторизации повара';