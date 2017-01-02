#!/usr/bin/bash
i=$1
elixir --name SoE$i@127.0.0.1 --cookie secret -S mix run --no-halt > logs/SoE$i
