[![Build & Verify](https://github.com/svenjacobs/leon/actions/workflows/build.yml/badge.svg)](https://github.com/svenjacobs/leon/actions/workflows/build.yml) [![Deploy](https://github.com/svenjacobs/leon/actions/workflows/deploy.yml/badge.svg)](https://github.com/svenjacobs/leon/actions/workflows/deploy.yml) <!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-50-orange.svg?style=flat-square)](#contributors-)
[![Reproducble Build Status](https://shields.rbtlog.dev/simple/com.svenjacobs.app.leon)](https://shields.rbtlog.dev/com.svenjacobs.app.leon)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

**Léon - The URL Cleaner** is an Android application which removes tracking and other obsolete
parameters from a URL before sharing. Its usage is simple, integrating into Android's standard
sharing mechanism.

Léon runs on Android 5.0 and later, is open source and does not contain any tracking or advertising
frameworks. Léon does not collect any data about you.

The benefits of removing tracking parameters are:

- Protects your and the recipient's privacy
- Improves readability of links
- Saves characters (in Tweets for example)

<img src="./etc/painting_mondstern.jpg" width="400" />

_Painting by [Mondstern](https://mastodon.green/@mondstern/111993655182793928) (CC BY-SA)_

## Installation

- [F-Droid](https://www.f-droid.org/packages/com.svenjacobs.app.leon/)  
  Signed through F-Droid. Find hashes of certificates [here](https://f-droid.org/en/docs/Release_Channels_and_Signing_Keys/).
- [IzzyOnDroid](https://apt.izzysoft.de/packages/com.svenjacobs.app.leon)  
  SHA256 of signing certificate: `3e:94:e0:c5:89:4c:17:5f:7f:15:f5:17:d4:46:fe:e0:71:10:12:b1:b1:e7:62:ed:0c:b5:b2:8c:2c:20:13:5d`
- [Google Play Store](https://play.google.com/store/apps/details?id=com.svenjacobs.app.leon)  
  SHA256 of signing certificate: `5B:D0:A8:62:7A:04:EB:8F:C1:F1:45:C6:A6:2B:7F:E5:7C:31:E3:87:05:1E:C8:8B:B2:FB:08:22:5B:6C:D2:0B`

## How to use

When sharing a link, select **URL Cleaner** as the receiving application. Then from within URL
Cleaner share the cleaned URL to the actual target application.

<img src="./app/src/main/res/drawable-nodpi/howto_pixel_5.webp" width="250" />

It's also possible to select editable text in any application which supports it, then in the
context menu choose Léon to sanitize text in place.

<img src="./etc/context_menu.png" />

## Issues & feedback

Did you find a bug or have an idea of how the app could be improved? Please report
[bugs](https://github.com/svenjacobs/leon/issues) or give
[feedback](https://github.com/svenjacobs/leon/discussions).

## API

It is possible to call Léon programmatically from other applications. See [API.md](./API.md) for
details.

## Technical implementation

This app is also meant as a blueprint for modern Android development, presenting and evaluating
recommended and cutting-edge technologies, patterns and libraries such as:

- [Kotlin](https://kotlinlang.org/) programming language
- Kotlin [coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- Jetpack [Compose](https://developer.android.com/jetpack/compose)
- Jetpack [Navigation](https://developer.android.com/guide/navigation)
- [Material Design 3](https://m3.material.io/)
- Dependency Injection pattern with custom implementation
- several other Jetpack & AndroidX libraries

## Alternatives

- [Untracker](https://github.com/zhanghai/Untracker) (Android)
- [Tarnhelm](https://github.com/lz233/Tarnhelm) (Android)
- [URLCheck](https://github.com/TrianguloY/UrlChecker) (Android)
- [LinkSheet](https://github.com/LinkSheet/LinkSheet) (Android)
- [ClearURLs](https://github.com/ClearURLs/Addon) (Browser)
- [Link Cleaner](https://linkcleaner.app/) (Web, PWA)
- [AmputatorBot](https://github.com/KilledMufasa/AmputatorBot) (Removes AMP from URLs)

## Contributors

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="http://svenjacobs.com"><img src="https://avatars.githubusercontent.com/u/255313?v=4?s=100" width="100px;" alt="Sven Jacobs"/><br /><sub><b>Sven Jacobs</b></sub></a><br /><a href="#maintenance-svenjacobs" title="Maintenance">🚧</a> <a href="https://github.com/svenjacobs/leon/commits?author=svenjacobs" title="Code">💻</a> <a href="https://github.com/svenjacobs/leon/commits?author=svenjacobs" title="Tests">⚠️</a> <a href="https://github.com/svenjacobs/leon/commits?author=svenjacobs" title="Documentation">📖</a> <a href="#ideas-svenjacobs" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://meet.deekshith.in"><img src="https://avatars.githubusercontent.com/u/2568945?v=4?s=100" width="100px;" alt="Deekshith Allamaneni"/><br /><sub><b>Deekshith Allamaneni</b></sub></a><br /><a href="#ideas-adeekshith" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jimcarst"><img src="https://avatars.githubusercontent.com/u/37066510?v=4?s=100" width="100px;" alt="jimcarst"/><br /><sub><b>jimcarst</b></sub></a><br /><a href="#ideas-jimcarst" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://openfoxblog.leven.dev"><img src="https://avatars.githubusercontent.com/u/39553804?v=4?s=100" width="100px;" alt="Damon Leven"/><br /><sub><b>Damon Leven</b></sub></a><br /><a href="#ideas-MCWertGaming" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/EasyVector"><img src="https://avatars.githubusercontent.com/u/25502419?v=4?s=100" width="100px;" alt="Yuhui Su"/><br /><sub><b>Yuhui Su</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/commits?author=EasyVector" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/bangzek"><img src="https://avatars.githubusercontent.com/u/5100725?v=4?s=100" width="100px;" alt="Zakaria"/><br /><sub><b>Zakaria</b></sub></a><br /><a href="#ideas-bangzek" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/pludi"><img src="https://avatars.githubusercontent.com/u/2112148?v=4?s=100" width="100px;" alt="Peter L."/><br /><sub><b>Peter L.</b></sub></a><br /><a href="#ideas-pludi" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/svenjacobs/leon/issues?q=author%3Apludi" title="Bug reports">🐛</a> <a href="https://github.com/svenjacobs/leon/commits?author=pludi" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jaswinder77"><img src="https://avatars.githubusercontent.com/u/31370528?v=4?s=100" width="100px;" alt="jaswinder77"/><br /><sub><b>jaswinder77</b></sub></a><br /><a href="#ideas-jaswinder77" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://www.onnno.nl/"><img src="https://avatars.githubusercontent.com/u/31142286?v=4?s=100" width="100px;" alt="Donno"/><br /><sub><b>Donno</b></sub></a><br /><a href="#ideas-Donnnno" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/slootsky"><img src="https://avatars.githubusercontent.com/u/5387861?v=4?s=100" width="100px;" alt="slootsky"/><br /><sub><b>slootsky</b></sub></a><br /><a href="#ideas-slootsky" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/svenjacobs/leon/issues?q=author%3Aslootsky" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://www.rakuten.com/r/TPSAMU?eeid=6991100"><img src="https://avatars.githubusercontent.com/u/185902?v=4?s=100" width="100px;" alt="TPS"/><br /><sub><b>TPS</b></sub></a><br /><a href="#ideas-TPS" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/svenjacobs/leon/issues?q=author%3ATPS" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Enkidu70"><img src="https://avatars.githubusercontent.com/u/1456895?v=4?s=100" width="100px;" alt="Robert"/><br /><sub><b>Robert</b></sub></a><br /><a href="#ideas-Enkidu70" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://cooper-davis.net"><img src="https://avatars.githubusercontent.com/u/8947634?v=4?s=100" width="100px;" alt="Ari Cooper Davis"/><br /><sub><b>Ari Cooper Davis</b></sub></a><br /><a href="#ideas-aricooperdavis" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/svenjacobs/leon/commits?author=aricooperdavis" title="Code">💻</a> <a href="https://github.com/svenjacobs/leon/commits?author=aricooperdavis" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ChristopherKing42"><img src="https://avatars.githubusercontent.com/u/8742930?v=4?s=100" width="100px;" alt="Christopher King"/><br /><sub><b>Christopher King</b></sub></a><br /><a href="#ideas-ChristopherKing42" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/chel1k"><img src="https://avatars.githubusercontent.com/u/70620574?v=4?s=100" width="100px;" alt="chel1k"/><br /><sub><b>chel1k</b></sub></a><br /><a href="#translation-chel1k" title="Translation">🌍</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/slmb3"><img src="https://avatars.githubusercontent.com/u/80601335?v=4?s=100" width="100px;" alt="slmb3"/><br /><sub><b>slmb3</b></sub></a><br /><a href="#ideas-slmb3" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/aha999"><img src="https://avatars.githubusercontent.com/u/50620416?v=4?s=100" width="100px;" alt="Klemen Skerbiš"/><br /><sub><b>Klemen Skerbiš</b></sub></a><br /><a href="#ideas-aha999" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/chexxor"><img src="https://avatars.githubusercontent.com/u/843161?v=4?s=100" width="100px;" alt="Alex Berg"/><br /><sub><b>Alex Berg</b></sub></a><br /><a href="#ideas-chexxor" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/osjasmine"><img src="https://avatars.githubusercontent.com/u/86544869?v=4?s=100" width="100px;" alt="osjasmine"/><br /><sub><b>osjasmine</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Aosjasmine" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/guerda"><img src="https://avatars.githubusercontent.com/u/230782?v=4?s=100" width="100px;" alt="Philip Gillißen"/><br /><sub><b>Philip Gillißen</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Aguerda" title="Bug reports">🐛</a> <a href="#ideas-guerda" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/svenjacobs/leon/commits?author=guerda" title="Code">💻</a> <a href="https://github.com/svenjacobs/leon/commits?author=guerda" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/aleksandarzekovic"><img src="https://avatars.githubusercontent.com/u/47242383?v=4?s=100" width="100px;" alt="Aleksandar Zekovic"/><br /><sub><b>Aleksandar Zekovic</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/commits?author=aleksandarzekovic" title="Code">💻</a> <a href="https://github.com/svenjacobs/leon/commits?author=aleksandarzekovic" title="Tests">⚠️</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ratatouille100"><img src="https://avatars.githubusercontent.com/u/146064808?v=4?s=100" width="100px;" alt="George Kalinin"/><br /><sub><b>George Kalinin</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Aratatouille100" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Gojikovi"><img src="https://avatars.githubusercontent.com/u/96128712?v=4?s=100" width="100px;" alt="Annie"/><br /><sub><b>Annie</b></sub></a><br /><a href="#ideas-Gojikovi" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/vjrj"><img src="https://avatars.githubusercontent.com/u/180085?v=4?s=100" width="100px;" alt="vjrj"/><br /><sub><b>vjrj</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Avjrj" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ThiccOwl"><img src="https://avatars.githubusercontent.com/u/105682582?v=4?s=100" width="100px;" alt="ThiccOwl"/><br /><sub><b>ThiccOwl</b></sub></a><br /><a href="#ideas-ThiccOwl" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/devnoname120"><img src="https://avatars.githubusercontent.com/u/2824100?v=4?s=100" width="100px;" alt="Paul"/><br /><sub><b>Paul</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Adevnoname120" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Glitchy-Tozier"><img src="https://avatars.githubusercontent.com/u/59611881?v=4?s=100" width="100px;" alt="Glitchy-Tozier"/><br /><sub><b>Glitchy-Tozier</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3AGlitchy-Tozier" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/serrq"><img src="https://avatars.githubusercontent.com/u/113377431?v=4?s=100" width="100px;" alt="serrq"/><br /><sub><b>serrq</b></sub></a><br /><a href="#ideas-serrq" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://amay.bio.link/"><img src="https://avatars.githubusercontent.com/u/50140643?v=4?s=100" width="100px;" alt="Amay"/><br /><sub><b>Amay</b></sub></a><br /><a href="#ideas-vrndavn" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gavanore"><img src="https://avatars.githubusercontent.com/u/117471800?v=4?s=100" width="100px;" alt="gavanore"/><br /><sub><b>gavanore</b></sub></a><br /><a href="#ideas-gavanore" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ma651851384"><img src="https://avatars.githubusercontent.com/u/36804279?v=4?s=100" width="100px;" alt="Matthias"/><br /><sub><b>Matthias</b></sub></a><br /><a href="#ideas-ma651851384" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://info.fschneider.me/"><img src="https://avatars.githubusercontent.com/u/45742226?v=4?s=100" width="100px;" alt="Felix Schneider"/><br /><sub><b>Felix Schneider</b></sub></a><br /><a href="#ideas-felixschndr" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gl-dev"><img src="https://avatars.githubusercontent.com/u/44951272?v=4?s=100" width="100px;" alt="gl-dev"/><br /><sub><b>gl-dev</b></sub></a><br /><a href="#ideas-gl-dev" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/esemismo22"><img src="https://avatars.githubusercontent.com/u/123868868?v=4?s=100" width="100px;" alt="esemismo22"/><br /><sub><b>esemismo22</b></sub></a><br /><a href="#ideas-esemismo22" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://www.codeinabox.com/"><img src="https://avatars.githubusercontent.com/u/2069958?v=4?s=100" width="100px;" alt="Mathew Attlee"/><br /><sub><b>Mathew Attlee</b></sub></a><br /><a href="#ideas-codeinabox" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/alex-mayorga"><img src="https://avatars.githubusercontent.com/u/649262?v=4?s=100" width="100px;" alt="alex-mayorga"/><br /><sub><b>alex-mayorga</b></sub></a><br /><a href="#ideas-alex-mayorga" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ixanza"><img src="https://avatars.githubusercontent.com/u/5535758?v=4?s=100" width="100px;" alt="ixanza"/><br /><sub><b>ixanza</b></sub></a><br /><a href="#ideas-ixanza" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://anonpaste.org/?eb066acd6e42709f#9MpdEzRdzJ5ntQcizLjN4mFMaKLi8ArdbQMLhwaFQ5ji"><img src="https://avatars.githubusercontent.com/u/77220130?v=4?s=100" width="100px;" alt="Linus Xanoni"/><br /><sub><b>Linus Xanoni</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Axanoni" title="Bug reports">🐛</a> <a href="#ideas-xanoni" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/rehork"><img src="https://avatars.githubusercontent.com/u/67607997?v=4?s=100" width="100px;" alt="rehork"/><br /><sub><b>rehork</b></sub></a><br /><a href="#translation-rehork" title="Translation">🌍</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://dev.jakedot.net/"><img src="https://avatars.githubusercontent.com/u/12250212?v=4?s=100" width="100px;" alt="JakeDot"/><br /><sub><b>JakeDot</b></sub></a><br /><a href="#ideas-jakedot" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/yedayak"><img src="https://avatars.githubusercontent.com/u/43016107?v=4?s=100" width="100px;" alt="yedayak"/><br /><sub><b>yedayak</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/commits?author=yedayak" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ngocanhtve"><img src="https://avatars.githubusercontent.com/u/125894401?v=4?s=100" width="100px;" alt="ngocanhtve"/><br /><sub><b>ngocanhtve</b></sub></a><br /><a href="#translation-ngocanhtve" title="Translation">🌍</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/BluePixel4k"><img src="https://avatars.githubusercontent.com/u/77979781?v=4?s=100" width="100px;" alt="BluePixel4k"/><br /><sub><b>BluePixel4k</b></sub></a><br /><a href="#ideas-BluePixel4k" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://beacons.ai/johnreed"><img src="https://avatars.githubusercontent.com/u/5510392?v=4?s=100" width="100px;" alt="John-Michael Reed"/><br /><sub><b>John-Michael Reed</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3AJohnReedLOL" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/JonathanBaby"><img src="https://avatars.githubusercontent.com/u/7895197?v=4?s=100" width="100px;" alt="Jonathan Baby"/><br /><sub><b>Jonathan Baby</b></sub></a><br /><a href="#ideas-JonathanBaby" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/NikunjKhangwal"><img src="https://avatars.githubusercontent.com/u/99175083?v=4?s=100" width="100px;" alt="NikunjKhangwal"/><br /><sub><b>NikunjKhangwal</b></sub></a><br /><a href="#ideas-NikunjKhangwal" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://aminda.eu"><img src="https://avatars.githubusercontent.com/u/831184?v=4?s=100" width="100px;" alt="Aminda Suomalainen ⚧"/><br /><sub><b>Aminda Suomalainen ⚧</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3AMikaela" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/rhubarbzed"><img src="https://avatars.githubusercontent.com/u/62303568?v=4?s=100" width="100px;" alt="rhubarb"/><br /><sub><b>rhubarb</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/issues?q=author%3Arhubarbzed" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/xyrolaith"><img src="https://avatars.githubusercontent.com/u/18216850?v=4?s=100" width="100px;" alt="xyrolaith"/><br /><sub><b>xyrolaith</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/commits?author=xyrolaith" title="Code">💻</a> <a href="#ideas-xyrolaith" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/jmbreuer"><img src="https://avatars.githubusercontent.com/u/1792037?v=4?s=100" width="100px;" alt="Joe Breuer"/><br /><sub><b>Joe Breuer</b></sub></a><br /><a href="https://github.com/svenjacobs/leon/commits?author=jmbreuer" title="Code">💻</a> <a href="https://github.com/svenjacobs/leon/issues?q=author%3Ajmbreuer" title="Bug reports">🐛</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->
