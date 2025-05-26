-- --------- Users ---------
INSERT INTO users (username, email, password, role)
VALUES ('ivan', 'ivan123@example.com',
        '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
       ('olga', 'olga123@example.com',
        '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN'),
       ('petro', 'petro123@example.com',
        '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER');

-- --------- Places ---------
INSERT INTO place (name, country, era, description, image_url)
VALUES ('Києво-Печерська лавра', 'Україна', 'XI століття',
        'Печерний монастирський комплекс та духовний центр православ’я.',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fb/%D0%9B%D0%B0%D0%B2%D1%80%D0%B0.jpg/1200px-%D0%9B%D0%B0%D0%B2%D1%80%D0%B0.jpg'),
       ('Стоунхендж', 'Велика Британія', '3000–2000 рр. до н.е.',
        'Мегалітична споруда у вигляді кам’яного кільця в Уїлтширі, ймовірно астрономічний спостережнець.',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Stonehenge2007_07_30.jpg/1200px-Stonehenge2007_07_30.jpg'),
       ('Великий Китайський мур', 'Китай', '7 ст. до н.е. – XVII ст. н.е.',
        'Система оборонних стін загальною довжиною понад 20 000 км, збудована різними династіями.',
        'https://tut-cikavo.com/images/1_new/the-great-wall-pix.jpg'),
       ('Піраміди Гізи', 'Єгипет', 'XXVI ст. до н.е.',
        'Комплекс трьох великих пірамід фараонів Хеопса, Хефрена і Менкаура біля Каїра.',
        'https://upload.wikimedia.org/wikipedia/commons/thumb/a/af/All_Gizah_Pyramids.jpg/1200px-All_Gizah_Pyramids.jpg'),
       ('Колізей', 'Італія', '70–80 рр. н.е.',
        'Античний амфітеатр у Римі, місце гладіаторських боїв і публічних видовищ.',
        'https://atlanttour.com.ua/wp-content/uploads/2023/10/photo_2023-10-16_16-01-37.jpg'),
       ('Тадж-Махал', 'Індія', '1632–1653 рр. н.е.',
        'Мармуровий мавзолей у Агрі, збудований імператором Шах-Джаханом на честь дружини Мумтаз-Махал.',
        'https://pohcdn.com/sites/default/files/styles/paragraph__hero_banner__hb_image__1880bp/public/hero_banner/Taj-Mahal.jpg'),
       ('Мачу-Пікчу', 'Перу', 'XV ст. н.е.',
        'Загублене місто інків на висоті 2 430 м у Андах, археологічний та культурний пам’ятник.',
        'https://images.unian.net/photos/2020_12/thumb_files/1200_0_1608126680-8618.jpg');

-- --------- Reviews ---------
INSERT INTO review (place_id, user_id, text, rating)
VALUES (1, 1, 'Вражаюче та спокійне місце, дуже сподобалось!', 5),
       (2, 2, 'Цікаво, але надто багато туристів.', 4),
       (3, 3, 'Вражає масштаб та історія стін, неймовірно!', 5),
       (1, 2, 'Дійсно чудова архітектура, рекомендую відвідати.', 4),
       (7, 1, 'Неймовірний краєвид з вершини, заряджає енергією.', 5);

-- --------- Reports ---------
INSERT INTO report (type, content)
VALUES ('popular_places',
        'Топ-3 найпопулярніших місць за кількістю відгуків: 1) Києво-Печерська лавра; 2) Стоунхендж; 3) Великий Китайський мур.'),
       ('top_ratings',
        'Місця з найвищим середнім рейтингом: 1) Мачу-Пікчу (5.0); 2) Києво-Печерська лавра (4.5); 3) Піраміди Гізи (4.0).');

-- --------- Favorites ---------
INSERT INTO favorite (user_id, place_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 1),
       (3, 7);
