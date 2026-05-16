# 🏠 HomePlugin

> Ein simples, leichtgewichtiges Home-Plugin für Paper & Purpur Server.  
> Erstellt von **BWS Arwin**

---

## 📋 Inhaltsverzeichnis

- [Über das Plugin](#über-das-plugin)
- [Voraussetzungen](#voraussetzungen)
- [Installation](#installation)
- [Commands](#commands)
- [Berechtigungen](#berechtigungen)
- [Wie es funktioniert](#wie-es-funktioniert)
- [Datenspeicherung](#datenspeicherung)
- [Bauen aus dem Quellcode](#bauen-aus-dem-quellcode)

---

## Über das Plugin

HomePlugin ermöglicht es Spielern, einen persönlichen Heimatpunkt zu setzen und sich jederzeit dorthin zu teleportieren. Das Plugin ist bewusst simpel gehalten – kein unnötiger Schnickschnack, nur das Wesentliche.

---

## Voraussetzungen

| Anforderung | Version |
|---|---|
| Paper oder Purpur | 1.21+ |
| Java | 17 oder höher |

---

## Installation

1. Lade die Datei `HomePlugin-1.0.0.jar` herunter.
2. Lege die JAR-Datei in den `plugins/`-Ordner deines Servers.
3. Starte den Server neu (oder führe `/reload confirm` aus).
4. Fertig – das Plugin ist sofort einsatzbereit!

Beim ersten Start wird automatisch ein Ordner `plugins/HomePlugin/` erstellt, in dem die Datei `homes.yml` angelegt wird.

---

## Commands

| Command | Beschreibung |
|---|---|
| `/sethome` | Setzt dein Home an deiner aktuellen Position. |
| `/home` | Teleportiert dich zu deinem gesetzten Home. |
| `/delhome` | Löscht dein gesetztes Home dauerhaft. |

### Beispiele

```
/sethome
→ ✔ Home gesetzt! [world | X: 100, Y: 64, Z: -200]

/home
→ ✔ Du wurdest zu deinem Home teleportiert! [world | X: 100, Y: 64, Z: -200]

/delhome
→ ✔ Dein Home wurde erfolgreich gelöscht.
```

---

## Berechtigungen

| Permission | Beschreibung | Standard |
|---|---|---|
| `homeplugin.sethome` | Erlaubt das Setzen eines Homes. | ✅ Alle Spieler |
| `homeplugin.home` | Erlaubt die Teleportation zum Home. | ✅ Alle Spieler |
| `homeplugin.delhome` | Erlaubt das Löschen eines Homes. | ✅ Alle Spieler |

Berechtigungen können über ein Permission-Plugin wie **LuckPerms** fein gesteuert werden, z. B. um bestimmten Gruppen bestimmte Commands zu verweigern.

---

## Wie es funktioniert

### `/sethome`

Wenn ein Spieler `/sethome` ausführt, wird seine aktuelle Position (Welt, X, Y, Z, Blickrichtung) gespeichert. Existiert bereits ein Home, wird es überschrieben. Die Daten werden sofort in die `homes.yml` geschrieben, sodass kein Home verloren geht, selbst wenn der Server unerwartet abstürzt.

### `/home`

Der Spieler wird asynchron zu seinem gespeicherten Home teleportiert. Das bedeutet, dass der Server-Hauptthread **nicht blockiert** wird – wichtig für die Performance bei vielen gleichzeitigen Spielern. Hat der Spieler noch kein Home gesetzt, erhält er eine freundliche Hinweismeldung.

### `/delhome`

Das Home des Spielers wird aus dem Arbeitsspeicher entfernt und die `homes.yml` wird sofort aktualisiert. Hat der Spieler kein Home, wird er darüber informiert.

---

## Datenspeicherung

Alle Homes werden in `plugins/HomePlugin/homes.yml` gespeichert. Das Format sieht so aus:

```yaml
homes:
  550e8400-e29b-41d4-a716-446655440000:
    world: world
    x: 100.5
    y: 64.0
    z: -200.5
    yaw: 90.0
    pitch: 0.0
```

Jeder Spieler wird über seine **UUID** identifiziert – Namensänderungen haben dadurch keinen Einfluss auf das gespeicherte Home.

---

## Bauen aus dem Quellcode

Voraussetzung: **Maven** und **Java 17** müssen installiert sein.

```bash
# Repository klonen oder ZIP entpacken
cd HomePlugin

# Plugin bauen
mvn clean package

# Fertige JAR liegt in:
# target/HomePlugin-1.0.0.jar
```

---

## Autor

**BWS Arwin**  
Erstellt mit ❤️ für die Minecraft-Community.
