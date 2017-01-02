#!/bin/bash
(cd soe_endpoint; bash start.sh > log) &
(cd soe; elixir run.exs > logs/log) &
(cd soe_client; iex --name client@127.0.0.1 --cookie secret -S mix)
