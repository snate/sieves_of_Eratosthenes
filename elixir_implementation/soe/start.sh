#!/usr/bin/bash
i=$1
elixir --name SoE$i@ushuntu -S mix run --no-halt
