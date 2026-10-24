# Before making this repository public

> **Publication blocker:** GitHub Push Protection reported Groq API keys and OpenRouter API keys in historical versions of `src/main/resources/application.properties`. Keeping the file out of the current checkout or adding it to `.gitignore` does **not** remove values already committed. Keep the repository private until every item below is complete.

## What the failed push reported

The supplied `git push` output shows that Git uploaded and GitHub processed the objects, but GitHub rejected the update to `refs/heads/main` with `GH013: Repository rule violations found`. The rule was **GitHub Push Protection**, and its specific violation was **“Push cannot contain secrets.”** The command ended with `main -> main (push declined due to repository rule violations)` and `error: failed to push some refs`. This is a remote rejection, not a local Git compression or network error; the attempted branch update did not succeed.

GitHub identified five secret findings in historical commits of the same file. The log identifies the credential types and locations, but does not print the key values:

| Finding | Location in `src/main/resources/application.properties` | Commits named by GitHub |
| --- | --- | --- |
| Groq API Key 1 | Line 26 in the first two commits; line 27 in the next three | `b92d692140d83db341778f96592c56406764d41c`, `06c3bf4d42dca0e5c8fe17468288e8ce9ec987f6`, `89d519f96a0817d85cb4670bb2d1a52480d994eb`, `2682ff49ce84e62a4fb0cd990aa6040e6520f5c9`, `785bdfb0a54aadbb5010ab03e52368e96533514d` |
| Groq API Key 2 | Line 27 | `6bb00372c6fa7b70b43c62dc512ef3dde30fec65`, `572cbba7d759cb5e93a708870f9451d5e7fb37c9`, `8b8cc03eb174d759215d42cc62b31e4f7be48531`, `06f74d7ab4f407db20da32fe3c20e4439da2a561`, `d0872823b520fa2fc2133d71f1d2794796dabd1a` |
| Groq API Key 3 | Line 27 | `c4930accb9e89dfd8f08056fe422cedc329b4a51` |
| OpenRouter API Key 1 | Line 45 | `c4930accb9e89dfd8f08056fe422cedc329b4a51`, `89d519f96a0817d85cb4670bb2d1a52480d994eb`, `6bb00372c6fa7b70b43c62dc512ef3dde30fec65`, `572cbba7d759cb5e93a708870f9451d5e7fb37c9`, `d0872823b520fa2fc2133d71f1d2794796dabd1a` |
| OpenRouter API Key 2 | Line 46 | `2682ff49ce84e62a4fb0cd990aa6040e6520f5c9` |

GitHub offered unblock links for the five findings. Those links would permit this push without removing the historical credentials; they are not remediation for exposed keys. Removing credentials only from the latest checkout, or adding the file to `.gitignore`, will not clear a finding in an earlier commit.

## Resolve the exposed credentials

- [ ] Revoke the affected Groq and OpenRouter keys in their provider dashboards, then create replacement keys. Treat the old keys as compromised even if they still work.
- [ ] Check whether any other credentials appeared in the same historical configuration file or elsewhere in Git history. Rotate every credential found, including database passwords, OAuth client secrets and other API keys where applicable.
- [ ] Store replacement credentials outside Git, such as environment variables or a local ignored configuration file. Keep only placeholders in `src/main/resources/application.properties.example`.
- [ ] Remove the real credentials from **all affected commits**, not just the latest file. Plan the history rewrite with anyone who has cloned the repository; rewritten commits require collaborators to resync their clones. Do not copy a secret into a replacement commit, PR description, screenshot or issue.
- [ ] Push the cleaned history and confirm that GitHub Push Protection and secret scanning report no remaining exposed credentials. Do not use an unblock/bypass link to publish active keys.
- [ ] Verify a fresh clone contains no real secrets in tracked files or reachable Git history, then make the repository public.

**Done means:** affected credentials are revoked, replacements are configured privately, historical copies are removed, a fresh clone is clean, and GitHub reports no unresolved secret findings before visibility changes.
