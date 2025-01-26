1. Клонирование репозитория из ветки demo
   git clone -b demo https://github.com/Lisa-1919/taxi.git

2. В файле .env надо указать данные для подключения к базе данных
3. Сборка артефактов
   mvn clean package -Pskip-tests

4. Сборка образов Docker
   docker compose build

5. Запуск контейнеров
   docker compose up
