# DIY module format (planned)

The starter does not yet load these packs. This document proposes a safe JSON-only format for theme/home customization.

```json
{
  "id": "example-night",
  "name": "Example Night",
  "type": "theme",
  "colors": {
    "background": "#07090D",
    "panel": "#12161E",
    "accent": "#D6B36A",
    "text": "#EDE8DC"
  },
  "homeCards": [
    { "title": "Search", "url": "https://search.brave.com/" }
  ]
}
```

Do not allow untrusted packs to execute arbitrary JavaScript or native code. Native functionality must be implemented and reviewed in Kotlin.
