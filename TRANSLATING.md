# Translating MusicBee Remote

MusicBee Remote is translated through ordinary pull requests on GitHub. There is no separate
translation platform or account to sign up for. This guide covers where the text lives, how to add
a language, and the rules a translation has to follow so the build accepts it.

Any string you leave untranslated shows in English, so a partial translation is still useful and
can be merged. You can finish the rest in later pull requests.

## Before you start

Open an issue (or comment on an existing one) saying which language you are working on. That way
two people don't translate the same language at the same time, and it gives us a place to
discuss terminology.

## Where the text lives

The app is split into modules, and each module has its own English strings in `src/main/res/values/`:

| Module | Files |
|---|---|
| `app` | `strings.xml` |
| `core/ui` | `strings.xml`, `plurals.xml` |
| `feature/content` | `strings.xml` |
| `feature/library` | `strings.xml`, `plurals.xml` |
| `feature/minicontrol` | `strings.xml` |
| `feature/misc` | `strings.xml` |
| `feature/playback` | `strings.xml` |
| `feature/settings` | `strings.xml` |
| `feature/widgets` | `strings.xml` |

Translate `strings.xml` and `plurals.xml` only. Skip `arrays.xml`: its labels point at strings
you already translate in `strings.xml`, and its values are stored in the user's settings, so a
translated value would break those settings.

A few strings appear in more than one module under the same name (for example `unknown_artist`).
Translate every copy, and use the same wording in each.

## Adding a language

1. Pick the folder name for your language. It is `values-` followed by the language code, plus `-r`
   and the region code when the region matters:

   | Language | Folder |
   |---|---|
   | Chinese (Simplified) | `values-zh-rCN` |
   | Chinese (Traditional) | `values-zh-rTW` |
   | German | `values-de` |
   | Portuguese (Brazil) | `values-pt-rBR` |
   | Spanish | `values-es` |

   Use the plain language code unless the language differs noticeably between regions.

2. In each module from the table above, create the folder next to `values/` and copy the English
   files into it. For Simplified Chinese in the library module that gives:

   ```
   feature/library/src/main/res/values-zh-rCN/strings.xml
   feature/library/src/main/res/values-zh-rCN/plurals.xml
   ```

3. Translate the text between the tags. Never change the `name` attribute: the app looks strings
   up by name.

   ```xml
   <string name="library_search_hint">Search your library</string>
   ```

   becomes

   ```xml
   <string name="library_search_hint">搜索你的音乐库</string>
   ```

4. Delete from your copy any entry marked `translatable="false"`, and any entry you don't want to
   translate yet. Removed entries fall back to English.

### Without installing anything

You can do all of this in the browser:

1. Open the module's `src/main/res` folder on GitHub.
2. Choose **Add file** > **Create new file**.
3. Type the full name, including the new folder, for example `values-zh-rCN/strings.xml`, and
   paste in the translated content.
4. GitHub offers to fork the repository and open a pull request for you. Add the remaining files
   to the same branch before you submit.

## Rules the build checks

The build runs Android lint over every translation. These mistakes fail it:

- **Placeholders.** Keep every `%1$s`, `%2$d`, `%d` and `%1$.1f` exactly as written, including the
  number and the letter after it. You may move them around in the sentence, which is what the
  numbers are for:

  ```xml
  <string name="library__sync_progress">Syncing %1$d of %2$d %3$s</string>
  ```

- **Plurals.** Give the quantities your language uses, and only those. Chinese, Japanese and
  Korean use a single form, `other`:

  ```xml
  <plurals name="album">
    <item quantity="other">%d 张专辑</item>
  </plurals>
  ```

  English uses `one` and `other`, Russian uses `one`, `few`, `many` and `other`. The
  [Unicode plural rules](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html)
  list them for every language.

- **Removed strings.** A translation whose English entry no longer exists fails the build. If a
  rebase brings that up, delete the entry.

## Formatting

- Escape apostrophes and double quotes with a backslash: `\'` and `\"`.
- Write `&amp;` for `&`, and `&lt;` for `<`. Text inside `<![CDATA[ ... ]]>` needs no escaping.
- `\n` is a line break. Keep it where the English has it.
- Use your language's own punctuation, quotation marks and ellipsis.
- Short labels such as `MUSIC` and `SCAN` are in capitals because of English design convention.
  Follow what reads naturally in your language.
- Keep names as they are: MusicBee, MusicBee Remote, Last.fm.

## What stays in English

Some text does not come from these files and is not part of the translation:

- **What's New.** The changelog is written in English only.
- **Help.** That tab loads the documentation website, which is in English.
- **Messages from the MusicBee plugin**, and your music library's tags.
- **The app name.** "MusicBee Remote" is the same in every language.

## Checking your translation

If you can build the app (see [CONTRIBUTING.md](CONTRIBUTING.md)):

```bash
./gradlew installGithubDebug
```

Then switch the app's language. On Android 13 and later go to **Settings** > **Apps** >
**MusicBee Remote** > **Language**. On older versions, change the phone's system language.

Go through the main screens (player, queue, library, settings, the widget) and look for text that
is cut off or wraps badly. Then run lint, which runs the same checks as CI:

```bash
./gradlew :app:lintGithubDebug
```

If you can't build the app, open the pull request anyway. CI runs the checks, and we can share
screenshots from a device with you.

## Keeping a translation up to date

New English strings show in English in your language until someone translates them. To find what
is missing, open any `strings.xml` in Android Studio and click **Open editor** in the banner at the
top. The Translations Editor lists every string across all modules and marks the missing ones for
each language.

## Submitting

- One language per pull request.
- Title it `feat: add <language> translation`, or `feat: update <language> translation` for
  later changes.
- Mention the issue you opened so it closes when the pull request merges.
