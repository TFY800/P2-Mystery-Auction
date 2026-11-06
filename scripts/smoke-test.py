#!/usr/bin/env python3
"""Optional standalone-JAR smoke test; uses only Python's standard library."""
import os
from pathlib import Path
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
JAR = ROOT / "target/mystery-auction-1.0.0.jar"
JAVA = str(Path(os.environ["JAVA_HOME"]).resolve() / "bin/java") if "JAVA_HOME" in os.environ else "java"


def run(directory, text):
    result = subprocess.run([JAVA, "-jar", str(JAR)], input=text, text=True,
                            capture_output=True, cwd=directory, timeout=20, check=True)
    assert not result.stderr, result.stderr
    return result.stdout


with tempfile.TemporaryDirectory(prefix="mystery-auction-") as directory:
    first = run(directory, "n\nSmoke\np\nnope\n49\n1001\n50\nw\ns\nq\n")
    for message in ["whole-dollar", "too small", "Insufficient funds", "Saved."]:
        assert message in first, first
    second_input = "l\nh\no\n" + "p\nw\n" * 9 + "q\n"
    second = run(directory, second_input)
    assert "Next round: 2/10" in second, second
    assert "Session complete. Final balance: $1000" in second, second
    scores = Path(directory) / "data/leaderboard.txt"
    assert len(scores.read_text().splitlines()) == 2
    third = run(directory, second_input)
    assert "Session complete. Final balance: $1000" in third, third
    assert len(scores.read_text().splitlines()) == 2, "Duplicate leaderboard entry"
    print("PASS: executable JAR new game, malformed/low/unaffordable bids, valid bid, withdrawal,")
    print("save, cross-process load, both history views, 10-round completion and replay deduplication.")
