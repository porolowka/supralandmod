Как собрать мод через GitHub Actions
====================================

Шаг 1. Создай аккаунт на GitHub
- Зайди на https://github.com
- Нажми Sign up (если нет аккаунта)
- Зарегистрируйся (нужен только email и пароль)

Шаг 2. Создай новый репозиторий
- Нажми "+" в правом верхнем углу -> New repository
- Имя: supralandmod
- Выбери Public
- НЕ ставь галочку "Add a README"
- Нажми Create repository

Шаг 3. Загрузи файлы
- В репозитории нажми "uploading an existing file"
- Перетащи ВСЕ файлы из папки mod/ (build.gradle, settings.gradle, 
  gradle.properties, gradlew, gradlew.bat, папку gradle/, папку src/)
- ВНИМАНИЕ: замени файл gradle/wrapper/gradle-wrapper.properties 
  на тот, что в этом архиве (с версией 8.7)
- Создай папку .github/workflows/ и положи туда файл build.yml
- Нажми Commit changes

Шаг 4. Дождись сборки
- Перейди на вкладку Actions в репозитории
- Там будет запускаться сборка (зелёный кружок = успех)
- Сборка идёт 3-5 минут

Шаг 5. Скачай готовый мод
- Когда сборка закончится (зелёная галочка), нажми на неё
- В самом низу будет раздел Artifacts
- Нажми supralandmod -> скачается ZIP
- Внутри ZIP будет файл supralandmod-1.0.0.jar
- Положи его в .minecraft/mods/

Всё! Мод готов.
