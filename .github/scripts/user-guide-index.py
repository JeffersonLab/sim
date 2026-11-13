#!/usr/bin/env python3
"""Write the user guide website's front page: links to the latest release's guide and to each
release's, newest first.

Usage: user-guide-index.py <output dir> <version folder>...
The version folders are the gh-pages branch's, such as v5.2.0; other names are ignored.
The page's title is the site_name in zensical.toml, read from the current directory. Needs
Python 3.11 for tomllib: `uv run --locked python` gives the project's.
"""
import html
import re
import sys
import tomllib
from pathlib import Path


def main():
    out = Path(sys.argv[1])
    with open("zensical.toml", "rb") as f:
        title = html.escape(tomllib.load(f)["project"]["site_name"])
    versions = sorted(
        {v for v in sys.argv[2:] if re.fullmatch(r"v\d+\.\d+\.\d+", v)},
        key=lambda v: tuple(int(n) for n in v[1:].split(".")),
        reverse=True,
    )
    items = "\n".join(
        f'      <li><a href="{html.escape(v)}/">{html.escape(v)}</a></li>' for v in versions
    )
    out.mkdir(parents=True, exist_ok=True)
    (out / "index.html").write_text(f"""<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>{title}</title>
    <style>
      body {{ font-family: system-ui, sans-serif; max-width: 40rem; margin: 2rem auto; padding: 0 1rem; }}
    </style>
  </head>
  <body>
    <h1>{title}</h1>
    <p><a href="latest/">The latest release's guide</a></p>
    <p>Each release's guide; the Help tab shows the version you are using:</p>
    <ul>
{items}
    </ul>
  </body>
</html>
""")
    # GitHub Pages serves the files as they are, without Jekyll
    (out / ".nojekyll").touch()


if __name__ == "__main__":
    main()
