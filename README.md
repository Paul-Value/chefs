# Chefs
Сервис для поиска шеф-поваров и заказа у них еды. 

- Интеграция OpenStreetMap API. Для поиска по адресу использует одну строку (например, "ул. Пушкина 23, Москва"). Через OpenStreetMap API происходит поиск подходящих вариантов и  возвращает результат на клиент. Пользователь выбирает нужный вариант (также расчитывается расстояние до адреса) и отправляет координаты (longitude/latitude) в эндпоинт для сохранения адреса.
 - Реализована кастомная аутентификации через заголовки X-Device-Id и X-Access-Code с использованием Spring Security и фильтров.  Заказчик (пользователь) передаёт в заголовке device_id (например, X-Device-Id), по которому его можно идентифицировать. Повар передаёт в заголовке access_code (например, X-Access-Code) вместе device_id.
 - Используются миграции через Liquibase для контроля изменений схемы БД (PostgreSQL)
 - SSE (Server-Sent Events): реализована потоковая передача данных через Spring WebFlux. После создания нового заказа со стороны пользователя, сервер отправляет SSE-сообщение всем подписанным поварам. Повар также может слушать SSE-эндпоинт для получения новых заказов в режиме реального времени
 - Интеграция Redis: настроено кэширование результатов OpenStreetMap API

## API

### Эндпоинты для пользователя (заказчика)
**1.1 Эндпоинт: Поиск координат по адресу (одна строка)**

<ins> POST /addresses/search </ins>

 Заголовки: X-Device-Id: <device_id>
 
Тело (Request Body):
`{
  "query": "пр. Пушкина 23, Москва"
}`

Описание:\
Вызывает  OpenStreetMap API для поиска координат и возвращает список подходящих адресов.\
Пример ответа:
```bash
{
"results": [
    {
      "formatted_address": "пр. Пушкина 23, Москва, Россия",
      "longitude": 76.945,
      "latitude": 43.256,
      "place_id": "ChIJN1..."
    },
    {
      "formatted_address": "ул. Пушкина 23/1, Москва, Россия",
      "longitude": 76.947,
      "latitude": 43.257,
      "place_id": "ChIJN2..."
    }
  ] 
}
```
  



**1.2. Эндпоинт: Сохранить (добавить/обновить) адрес**
 
<ins> POST /addresses </ins> 
 
Заголовки: X-Device-Id: <device_id>

Тело (Request Body):
```bash
{
  “Place_id”: “fdsfdaj”
  "entrance": "1",
  "floor": "3",
  "apartment": "12"
}
```

Описание:\
Сохраняет (или обновляет) адрес пользователя и его координаты в БД.(long, lat берется с place_id)\
Ответ:
```json
{
  "message": "Address updated successfully"
}
```
**1.3. Эндпоинт: Добавление/изменение персональных данных**

<ins> POST /users/personal-data </ins>

Заголовки: X-Device-Id: <device_id>\

Тело (Request Body):
```json
{
  "name": "Иван",
  "phone": "+77001112233"
}
```
Описание:\
Добавляет/обновляет имя и номер телефона пользователя (заказчика).\
Ответ:
```json
{
  "message": "Personal data updated successfully"
}
```

**1.4. Эндпоинт: Получение списка ближайших поваров**

<ins> GET /chefs </ins>

Заголовки: X-Device-Id: <device_id>

Описание:\
Получает из БД координаты пользователя и выводит список поваров, которые:\
1. Находятся в радиусе 5 км от координат пользователя.\
2. У которых is_working = true.\
Ответ (пример):
```json
[
  {
    "chef_id": 123,
    "photo_url": "https://example.com/photo.jpg",
    "description": "Готовлю традиционные блюда",
    "phone": "+77001112233",
    "is_working": true,
    "foods": [
      {
        "food_id": 456,
        "name": "Плов",
        "description": "Настоящий узбекский плов",
        "price": 1500,
        "photo_url": "https://example.com/food.jpg"
      }
    ]
  }
]
```

**1.5. Эндпоинт: Получение детальной информации о конкретном поваре**

<ins> GET /chefs/{chef_id} </ins>

Заголовки: X-Device-Id: <device_id>

Описание:\
Возвращает данные конкретного повара (при условии, что is_working = true или, по бизнес-логике, даже если is_working = false, но пользователь может просматривать).\
Ответ (пример):
```json
{
  "chef_id": 123,
  "photo_url": "https://example.com/photo.jpg",
  "description": "Готовлю традиционные блюда",
  "phone": "+77001112233",
  "is_working": true,
  "foods": [
    {
      "food_id": 456,
      "name": "Плов",
      "description": "Настоящий узбекский плов",
      "ingredients": ["лук", "сыр"],
      "price": 1500,
      "photo_url": "https://example.com/food.jpg"
    }
  ]
}
```

**1.6. Эндпоинт: Создание заказа**

<ins> POST /orders </ins>

Заголовки: X-Device-Id: <device_id>

Тело (Request Body):
```json
{
  "items": [
    { "food_id": 456, "quantity": 2 },
    { "food_id": 789, "quantity": 1 }
  ],
  "comment": "Положите, пожалуйста, салфетки"
}
```

Описание:
1. Сервис проверяет, заполнены ли у пользователя персональные данные и есть ли адрес.
2. Рассчитывается цена (сумма price * quantity по всем блюдам).
3. Создаётся заказ со статусом по умолчанию (in_cooking).
4. После успешного создания заказа сервер отправляет SSE-событие для поваров (либо конкретному повару, если известно, к кому относится еда).
Ответ:
```json
{
  "order_id": 101112,
  "status": "created",
  "total_price": 2500
}
```

**1.7. Эндпоинт: Получение цены**

<ins> POST /orders/price </ins>

Заголовки: X-Device-Id: <device_id>

Тело (Request Body):
```json
{
  "items": [
    { "food_id": 456, "quantity": 2 },
    { "food_id": 789, "quantity": 1 }
  ],
}
```

Ответ:
```json
{
  "total_price": 2500
}
```

**1.8. Эндпоинт: Получение текущих заказов пользователя**

<ins> GET /orders/current </ins>

Заголовки: X-Device-Id: <device_id>

Описание:\
Возвращает активные (не завершённые) заказы пользователя.

Ответ (пример):
```json
[
  {
    "order_id": 101112,
    "chef_name": "Повар Ахмет",
    "chef_phone": "+77001112233",
    "status": "in_cooking",
    "created_at": "2025-01-01T12:00:00Z",
    "total_price": 2500,
    "items": [
      { "food_id": 456, "name": "Плов", "quantity": 2, "price": 1500 }
    ]
  }
]
```

**1.9. Эндпоинт: Получение истории заказов пользователя**

<ins> GET /orders/history </ins>

Заголовки: X-Device-Id: <device_id>

Описание:
Возвращает завершённые или отменённые заказы пользователя.

Ответ (пример):
```json
[
  {
    "order_id": 101100,
    "chef_name": "Повар Ахмет",
    "status": "completed",
    "created_at": "2024-12-31T18:00:00Z",
    "total_price": 2000,
    "items": [
      { "food_id": 789, "name": "Манты", "quantity": 3, "price": 700 }
    ]
  }
]
```

### Эндпоинты для повара

**2.1. Эндпоинт: Проверка (валидация) access-кода**

<ins> POST /chefs/validate-access </ins>

Заголовки: X-Access-Code: <access_code>

Описание:\
Проверяет, существует ли повар в базе с таким access_code. При успехе возвращает идентификатор повара или успех.

Ответ (пример):
```json
{
  "chef_id": 123,
  "message": "Access code is valid"
}
```

Ошибки:\
401 Unauthorized: неверный или отсутствующий access_code.

**2.2. Эндпоинт: Смена рабочего статуса повара**

<ins> POST /chefs/working-status </ins>

Заголовки: X-Access-Code: <access_code>

Тело (Request Body):
```json
{
  "is_working": true
}
```

Описание:\
Устанавливает повару рабочий статус (is_working = true/false).

Ответ:
```json
{
  "message": "Working status updated successfully",
  "is_working": true
}
```

**2.3. SSE-эндпоинт: Просмотр поступающих заказов в реальном времени**

<ins> GET /chefs/orders/stream </ins>

Заголовки: X-Access-Code: <access_code>

Описание:
Открывает SSE-подключение (тип text/event-stream), по которому повар получает информацию о новых заказах.

Пример события (SSE):
```
event: new_order
data: {"order_id": 101112, "items": [...], "customer_id": 55, ... }
```

**2.4. Эндпоинт: Принятие или отказ от заказа**

<ins> POST /chefs/orders/{order_id}/response </ins>

Заголовки: X-Access-Code: <access_code>

Тело (Request Body):
```json
{
  "action": "accept"  // или "reject"
  "reject_comment": "Извините, я не успеваю"
}
```

Если action = "reject", то поле reject_comment — обязательное.

Описание:\
Позволяет повару принять или отклонить заказ.
- При принятии заказа статус может меняться на in_cooking.
- При отказе заказ становится canceled (или “rejected” — в зависимости от бизнес-логики), а в поле комментария сохраняется причина.

Ответ (пример):
```json
{
  "order_id": 101112,
  "status": "in_cooking",
  "message": "Order accepted"
}
```
либо
```json
{
  "order_id": 101112,
  "status": "canceled",
  "message": "Order rejected: Извините, я не успеваю"
}
```

**2.5. Эндпоинт: Смена статуса заказа**

<ins> POST /chefs/orders/{order_id}/status </ins>

Заголовки: X-Access-Code: <access_code>

Тело (Request Body):
```json
{
  "status": "delivering" // или in_cooking, completed, canceled
}
```

Описание:\
Изменяет статус уже принятого заказа. Например, повар поменял статус на delivering, когда заказ передан курьеру.

Ответ (пример):
```json
{
  "order_id": 101112,
  "status": "delivering",
  "message": "Order status updated"
}
```


**Примечание**
- При создании заказа (эндпоинт /orders):
1. Проверяет наличие device_id (customer_id).
2. Проверяет валидность адреса и персональных данных.
3. Определяет повара для каждого блюда по food.chef_id.
4. Сохраняет заказ.
5. Отправляет SSE-событие с деталями нового заказа, чтобы повар(ы) могли увидеть поступление.
- При SSE-подписке повар получает новые заказы или обновления статусов. Пример события:
```
event: new_order
data: {"order_id":123, "items":[...] }
```
Или:
```
event: order_status_update
data: {"order_id":123, "new_status":"delivering" }
```
