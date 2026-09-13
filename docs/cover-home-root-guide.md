# Гайд: zruchno как home-лаунчер cover-экрана (Mix Flip) через root

Документ описывает единственный путь сделать `com.zruchno` дефолтным лаунчером
внешнего (cover) дисплея Xiaomi MIX Flip. Без root это невозможно: FlipPolicy в
`system_server` жёстко назначает `currentUserFlipHome = com.miui.fliphome` и при
любом home-жесте на cover явно запускает `com.miui.fliphome/.FlipLauncher`
(подтверждено logcat-диагностикой, см. §1).

**Все флеш-шаги вы выполняете сами.** Этот документ — только инструкция и план
прототипа хука. Каждый шаг имеет аварийный выход.

---

## 0. Опорные факты (проверено на устройстве)

| Параметр | Значение |
|---|---|
| Устройство | Xiaomi MIX Flip (codename `ruyi`), Global |
| Модель | `2405CPX3DG` |
| Прошивка | `OS3.0.303.0.WNIMIXM`, Android 16 (SDK 36), HyperOS 3 |
| Build | `BP2A.250605.031.A3` |
| Bootloader | **заблокирован** (`ro.boot.flash.locked=1`, `vbmeta locked`, `verifiedboot=green`) |
| Роль HOME (внутр. экран) | `com.zruchno` (уже установлена, работает) |
| Роль `android.app.role.SECONDARY_HOME` | на этой прошивке **не существует** (`Unknown role`) |
| Cover-home задача | `Task e5d4340 #2 type=home I=com.miui.fliphome/.FlipLauncher` |

Ключевые классы/методы в `system_server` (из logcat):
- `com.android.server.wm.FlipPolicyLauncherSwitch`
  - `updateHomeProcess` → `currentUserFlipHome=ActivityRecord{...FlipLauncher t2}`
  - `processHomeForUnFold` (в `FlipPolicyLauncherSwitch2`): выбирает inner/outer
  - `reverseHomeTask`, `getRootHomeTask`
- `com.android.server.wm.FlipPolicyActivityIntercept` — не блокирует старт
  zruchno на cover, если он запущен через флипхом-иконку (`setForceDisplayCompatMode
  allow start with app com.zruchno`).

Наблюдение: zruchno уже успешно работает на cover **как приложение** (по тапу
иконки из флипхом-списка, который настраивает MixFlipTool). Меняется только
поведение home-жеста.

---

## 1. Зачем root (кратко)

- Категория `SECONDARY_HOME` резолвится, но FlipPolicy **не** выбирает по ней —
  он хардкодит флипхом через явный `cmp` и поле `currentUserFlipHome`.
- Роли `android.app.role.SECONDARY_HOME` на прошивке нет (проверено `cmd role`).
- Официальная «вторая точка входа» для третьих лиц — action `miui.fliphome.default`
  — это отдельный экран **внутри** флипхома, а не замена лаунчера.
- Готовых Xposed-модулей с подменой cover-home нет (MixFlipMod и HyperCeiler
  только дополняют флипхом). Нужен собственный хук.

---

## 2. Предусловия разблокировки bootloader (политика Xiaomi 2026)

1. Mi-аккаунт активен **не менее 30 дней**.
2. Приложение **Xiaomi Community ≥ 5.3.31**, регион профиля — **Global**.
3. Учётная запись разблокировала **не более 3 устройств за год**.
4. Устройство — официальная **глобальная** прошивка (наша — да: `...WNIMIXM`).
5. После одобрения заявки — ожидание **72 часа** (раньше 168).
6. **Данные будут стёрты** в момент разблокировки.

---

## 3. Шаг D0 — Полное резервное копирование (обязательно)

Разблокировка bootloader безвозвратно стирает userdata. До неё:

1. `adb backup` приложений (или Xiaomi Mi Cloud / локальный бэкап в MiUI).
2. Связки ключей/2FA, банковские приложения — перевыпустить заранее.
3. Сохранить `keystore.properties` репозитория в надёжное место (вне git).
4. Сделать снимок текущего состояния настроек (по желанию):
   `adb shell settings list global / system / secure`.

Аварийный выход D0: ничего нельзя сломать — это чистый read-only.

---

## 4. Шаг D1 — Установить привязку аккаунта (на устройстве)

1. Настройки → О телефоне → 7 тапов по версии ОС (включить режим разработчика).
2. Дополнительные настройки → Для разработчиков:
   - «Заводская разблокировка» (OEM unlocking) — **включить**;
   - «Отладка по USB» — включить;
   - «Статус Mi Unlock» → «Связать аккаунт с устройством» (только мобильный
     интернет, Wi-Fi выключить).
3. Дождаться «Связано» / таймера привязки.

---

## 5. Шаг D2 — Подать заявку

1. Xiaomi Community → вкладка **Me** → **Unlock bootloader** → **Apply for unlocking**.
2. При ошибке сервера — повторять с паузой (заявки квотируются; по опыту удобно
   подавать около 00:00 по Пекину / 19:00 МСК).
3. После одобрения статус станет «Granted until …» (обычно на 365 дней).

---

## 6. Шаг D3 — Разблокировать через ПК

1. Скачать официальный **Mi Unlock Tool** (доступен по ссылке из форума
   https://en.miui.com/unlock/ или c.mi.com).
2. Войти в тот же Mi-аккаунт в тулзе.
3. Выключить телефон, войти в **fastboot** (Power + VolDown).
4. Подключить к ПК, нажать **Unlock**.
5. При назначении таймера — повторить по истечении (обычно 72 ч).

Аварийный выход D3: если разблокировать не вышло — просто не продолжаем,
устройство остаётся стоковым. Данные уже стёрты (это часть процесса).

Ожидаемое состояние: `ro.boot.flash.locked=0`, `vbmeta device_state=unlocked`,
при загрузке показан значок «unlocked».

---

## 7. Шаг D4 — Root через Magisk

Рекомендованный стабильный: `Magisk ≥ v30.7` (поддержка Android 16 QPR2);
актуальный на 2026-09: `v31.0`.

1. Выкачать **stock-прошивку** точно версии `OS3.0.303.0.WNIMIXM` (fastboot ROM
   для `ruyi` Global; источники: xiaomirom.com, mifirm.net, miuirom.org).
2. Распаковать, из `images/` взять `boot.img`.
3. Установить Magisk APK на телефон, **Patch a File** → `boot.img` →
   получить `magisk_patched-<…>.img`.
4. Перезагрузить в fastboot, прошить:
   - `fastboot flash boot magisk_patched-<…>.img`
   - `fastboot reboot`
5. Проверить: Magisk Manager показывает версию и root (`adb shell su -c id`).

Аварийный выход D4:
- Если bootloop — fastboot-прошивка стоковой `boot.img` из той же прошивки вернёт
  к стоку за 30 секунд (флипхом на месте, зовручно переустановить APK).
- Полный возврат к стоку: `miflash` с `flash_all.bat` (данные чистятся) либо
  `flash_all_except_storage.bat` (данные сохраняются).

---

## 8. Шаг D5 — Фреймворк Xposed (Vector / LSPosed)

MixFlipMod требует `libxposed API 101`. Подходят:

- **Vector v2.1+** (преемник LSPosed, `JingMatrix/Vector`) — первая версия
  полностью на API 101; официальный Zygisk-модуль для Magisk/KernelSU/APatch.
- Классический **LSPosed v1.11.0** (z466, API 101), если предпочтение LSPosed.

1. В Magisk: включить **Zygisk** → Ребут.
2. Установить `Vector-v2.x-Release.zip` через Magisk как модуль → Ребут.
3. Войти в Vector manager (доступен через уведомление / иконку), убедиться, что
   модули подхватываются.
4. Установить `com.parallelc.mixflipmod` APK (с releases GitHub), включить в
   Vector, скоуп **system framework** → Ребут.

Аварийный выход D5: выключить модуль в Vector → Ребут. Фреймворк при этом
остаётся, но любой хук мгновенно деактивируется. Флипхом работает по стоку.

> Примечание: MixFlipMod сам по себе не меняет cover-home. Ставим его как
> проверку окружения (API 101, скоуп system_server) перед собственным модулем.

---

## 9. Шаг D6 — Собственный модуль: хук FlipPolicy (прототип)

План hook-модуля (отдельный APK `module.zruchno.coverhome`, scope `system`):

Цель: заставить FlipPolicy считать «outer home» = `com.zruchno/.MainActivity`
вместо `com.miui.fliphome/.FlipLauncher`.

1. **Определить точные сигнатуры на прошивке.** После root вытащить
   `/system/framework/services.jar`, декомпилировать (JADX), найти
   `FlclipPolicyLauncherSwitch` и `FlipPolicyLauncherSwitch2`,
   выписать точные методы `updateHomeProcess` и `processHomeForUnFold`.
   (Имена в logcat реальны, но сигнатуры нужно верифицировать на версию
   `OS3.0.303.0.WNIMIXM` — код меняется от билда к билду.)
2. **Хук `updateHomeProcess`:** перехватить присвоение `currentUserFlipHome`
   и подставить ActivityRecord/package `com.zruchno` (или вернуть true мимо
   оригинальной ветки, заставив FlipPolicy взять zruchno из rootHomeTasks).
   zruchno уже присутствует в `rootHomeTasks` как inner-home — проще научить
   SelectLocked цеплять его, чем подсовывать чужой ActivityRecord.
3. **Хук `processHomeForUnFold`:** поменять местами inner/outer —
   outer должен резолвиться в `Task{... com.zruchno}`. Осторожно с реальной
   «разворачиванием» inner-задачи: внутренний экран всё равно должен показать
   zruchno, т.е. перестановка нужна только на этапе выбора outer-home-мишени.
4. **Скоуп:** только `system` (system_server). Каждый хук оборачивать
   `runCatching` + логирование; любые исключения — `chain.proceed()`.
5. **Безопасность прототипа:** модуль включается в Vector и сразу проверяется
   на home-жесте cover. При неправильном поведении — выключить модуль, reboot.

Аварийный выход D6 (главный): **выключить модуль в Vector → reboot.** Система
возвращается к fliphome как cover-home. Флипхом нигде не деинсталлируется и не
отключается — удаление модуля восстанавливает сток на 100%.

---

## 10. Проверка результатов

После включения хука и reboot:

1. Сложить телефон → home-жест → должен появиться zruchno на display 0.
   Проверить: `dumpsys activity activities | grep 'type=home'` → задача outer
   с `A=com.zruchno`.
2. Развернуть → home → зовручно на внутреннем (inner-задача не должна
   сломаться).
3. Лог: `FlipPolicyLauncherSwitch: updateHomeProcess` → `currentUserFlipHome`
   должен ссылаться на `com.zruchno`.
4. Регрессия: при выключенном модуле все поведения совпадают со стоком.

---

## 11. Аварийные выходы (сводка)

| Состояние | Как вернуть сток |
|---|---|
| До unlock | Ничего не сломать нельзя; просто не продолжать |
| После unlock, до flash | Загрузиться в fastboot → Mi Unlock Tool → Lock (или flash_all_lock) |
| botloop после Magisk | fastboot flash boot <стоковый boot.img> → reboot |
| Проблема после Vector | Удалить Vector.ZIP через Magisk (Recovery/Manager), reboot |
| Хук отвал/поведение не то | Отключить `module.zruchno.coverhome` в Vector → reboot |
| Полный сброс | miflash, `flash_all.bat` (чисто) / `flash_all_except_storage.bat` (с данными) |
| Восстановление ПО | `com.miui.fliphome` остаётся в прошивке всегда; `com.zruchno` — обычный APK |

Правило безопасности: **каждое изменение кладёт флипхом на место сам факт
отключения модуля.** До тех пор, пока не удаляем системные приложения и не
правим классы необратимо, brick исключён.

---

## 12. Связанные ограничения (после root)

- OTA-обновления HyperOS глобальной прошивкой сложнее: при разблокированном
  bootloader обновления могут требовать полного flash заново (см. §D4-выход).
- Банковские/Google Pay DRM (Widevine L1) могут деградировать до L3 при
  разблокированном bootloader — проверить до перехода на root-путь.
- After unlock: «Найти устройство», часть secure-функций Xiaomi могут не работать.

---

## 13. Приложения в репозитории (для справки)

- Хук-модуль на этапе прототипа можно строить на шаблоне MixFlipMod (AGPL-3.0,
  `parallelcc/MixFlipMod`), или отдельным standalone-модулем (libxposed API 101,
  scope `system`). Соблюдать лицензию AGPL при форке.
- Манифест zruchno сейчас объявляет `android.intent.category.SECONDARY_HOME`
  (эксперимент); на текущей прошивке это не влияет на cover-home и может быть
  удалено после введения хука.