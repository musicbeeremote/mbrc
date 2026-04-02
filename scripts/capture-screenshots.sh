#!/usr/bin/env bash
set -euo pipefail

# Interactive screenshot capture script for MusicBee Remote documentation.
# Prerequisites: adb (Android SDK), optionally cwebp (libwebp) for conversion.
#
# Usage: ./scripts/capture-screenshots.sh [output-dir]
#
# The script guides you through each screen, waits for you to navigate,
# then captures and names the screenshot automatically.
# If a screenshot already exists, it asks whether to retake or skip.

OUTPUT_DIR="${1:-screenshots}"
DOCS_DIR="${2:-}"

# Check prerequisites
if ! command -v adb &>/dev/null; then
  echo "Error: adb not found. Install Android SDK platform-tools." >&2
  exit 1
fi

if ! adb get-state &>/dev/null; then
  echo "Error: No device connected. Connect a device and enable USB debugging." >&2
  exit 1
fi

HAS_CWEBP=false
if command -v cwebp &>/dev/null; then
  HAS_CWEBP=true
fi

mkdir -p "$OUTPUT_DIR"

DEVICE_MODEL=$(adb shell getprop ro.product.model | tr -d '\r' | tr ' ' '-')
echo "Device: $DEVICE_MODEL"
echo "Output: $OUTPUT_DIR/"
if [[ -n "$DOCS_DIR" ]]; then
  echo "Docs:   $DOCS_DIR/"
  mkdir -p "$DOCS_DIR"
fi
echo ""

# Define screens to capture
# Format: filename:title:instructions
declare -a SCREENS=(
  # Player
  "01_player_dark:Player (dark theme):Open the app with a track playing. Use dark theme."
  "02_player_light:Player (light theme):Switch to light theme (Settings > Theme), return to player."
  "03_player_bottom_sheet:Player overflow menu:Tap the three-dot menu on the player screen."
  "04_track_details:Track details:In the player overflow menu, tap Track Details."
  "05_lyrics:Lyrics overlay:Tap the lyrics button on the player to open the lyrics overlay."
  "06_rating:Rating bar:Open the player overflow menu to show the rating bar."

  # Navigation
  "07_drawer_connected:Drawer (connected):Open the navigation drawer while connected."
  "08_drawer_disconnected:Drawer (disconnected):Disconnect, then open the drawer to show offline state."

  # Queue
  "09_now_playing:Now Playing queue:Navigate to Queue from the drawer."

  # Library
  "10_library_genres:Library - Genres:Navigate to Library > Genres tab."
  "11_library_artists:Library - Artists:Switch to the Artists tab."
  "12_library_albums_list:Library - Albums (list):Switch to Albums tab in list view."
  "13_library_albums_grid:Library - Albums (grid):Toggle to grid view on Albums tab."
  "14_library_tracks:Library - Tracks:Switch to the Tracks tab."
  "15_sort_bottom_sheet:Sort bottom sheet:Tap the sort icon to open sort options."
  "16_genre_artists:Genre drilldown:Tap a genre to see its artists."
  "17_artist_albums:Artist drilldown:Tap an artist to see their albums."
  "18_album_tracks:Album drilldown:Tap an album to see its tracks."

  # Playlists & Radio
  "19_playlists:Playlists:Navigate to Playlists from the drawer."
  "20_radio:Radio stations:Navigate to Radio from the drawer."

  # Settings & Connections
  "21_connection_manager:Connection manager:Navigate to Connection Manager from the drawer."
  "22_connection_form:Add connection:Tap the FAB to open the add connection form."
  "23_settings:Settings:Navigate to Settings from the drawer."

  # Mini control & Notification
  "24_mini_control:Mini control:Navigate to Library or Playlists to show the mini player bar."
  "25_notification:Notification:Pull down the notification shade to show the media notification."

  # Widget
  "26_widget:Home screen widget:Go to the home screen showing the MusicBee Remote widget."

  # Additional features
  "27_output_selection:Output selection:On the player, tap the speaker icon to open output selection."
  "28_queue_context_menu:Queue context menu:In the Queue, long-press a track to show Go to Album/Artist."
  "29_library_menu:Library overflow menu:In Library, tap the three-dot menu to show Album artists only toggle."
  "30_track_context_menu:Track context menu:In Library > Tracks, long-press a track to show all actions."
  "31_genre_context_menu:Genre context menu:In Library > Genres, long-press a genre to show Go to Albums."
  "32_edit_connection:Edit connection:In Connection Manager, tap the edit icon on a connection."
)

TOTAL=${#SCREENS[@]}
CAPTURED=0
SKIPPED=0
EXISTING=0

echo "=== MusicBee Remote Screenshot Capture ==="
echo "Total screens: $TOTAL"
echo ""
echo "For each screen:"
echo "  - Navigate to the screen on your device"
echo "  - Press Enter to capture"
echo "  - Press 's' + Enter to skip"
echo "  - Press 'q' + Enter to quit"
echo ""
echo "-------------------------------------------"

for entry in "${SCREENS[@]}"; do
  IFS=':' read -r filename title instructions <<< "$entry"

  # Determine output file extension
  if $HAS_CWEBP; then
    OUTPUT_FILE="$OUTPUT_DIR/${filename}.webp"
  else
    OUTPUT_FILE="$OUTPUT_DIR/${filename}.png"
  fi

  # Check if screenshot already exists
  if [[ -f "$OUTPUT_FILE" ]]; then
    echo ""
    echo "[$((CAPTURED + SKIPPED + EXISTING + 1))/$TOTAL] $title"
    echo "  → Already exists: $OUTPUT_FILE"
    echo -n "  Retake? [Enter=skip / r=retake / q=quit]: "

    read -r input || true
    case "${input:-}" in
      r|R)
        ;; # Fall through to capture
      q|Q)
        echo "Quitting."
        break
        ;;
      *)
        echo "  Kept existing."
        EXISTING=$((EXISTING + 1))
        continue
        ;;
    esac
  else
    echo ""
    echo "[$((CAPTURED + SKIPPED + EXISTING + 1))/$TOTAL] $title"
    echo "  → $instructions"
    echo -n "  Ready? [Enter=capture / s=skip / q=quit]: "

    read -r input || true
    case "${input:-}" in
      s|S)
        echo "  Skipped."
        SKIPPED=$((SKIPPED + 1))
        continue
        ;;
      q|Q)
        echo "Quitting."
        break
        ;;
    esac
  fi

  # Capture screenshot
  REMOTE_PATH="/sdcard/screenshot_tmp.png"
  LOCAL_PNG="$OUTPUT_DIR/${filename}.png"
  LOCAL_WEBP="$OUTPUT_DIR/${filename}.webp"

  adb shell screencap -p "$REMOTE_PATH" || { echo "  ✗ Capture failed"; continue; }
  adb pull "$REMOTE_PATH" "$LOCAL_PNG" >/dev/null 2>&1 || { echo "  ✗ Pull failed"; continue; }
  adb shell rm "$REMOTE_PATH" 2>/dev/null || true

  # Convert to webp if cwebp is available
  if $HAS_CWEBP; then
    cwebp -q 85 "$LOCAL_PNG" -o "$LOCAL_WEBP" -quiet
    rm "$LOCAL_PNG"
    echo "  Saved: $LOCAL_WEBP"
  else
    echo "  Saved: $LOCAL_PNG"
  fi

  # Copy to docs directory if specified
  if [[ -n "$DOCS_DIR" ]]; then
    if $HAS_CWEBP; then
      cp "$LOCAL_WEBP" "$DOCS_DIR/"
      echo "  Copied to: $DOCS_DIR/${filename}.webp"
    else
      cp "$LOCAL_PNG" "$DOCS_DIR/"
      echo "  Copied to: $DOCS_DIR/${filename}.png"
    fi
  fi

  CAPTURED=$((CAPTURED + 1))
done

echo ""
echo "=== Done ==="
echo "Captured: $CAPTURED"
echo "Existing: $EXISTING (kept)"
echo "Skipped:  $SKIPPED"
echo "Output:   $OUTPUT_DIR/"

if ! $HAS_CWEBP && [ $((CAPTURED + EXISTING)) -gt 0 ]; then
  echo ""
  echo "Tip: Install cwebp to auto-convert to webp:"
  echo "  sudo pacman -S libwebp   # Arch"
  echo "  sudo apt install webp    # Debian/Ubuntu"
fi
