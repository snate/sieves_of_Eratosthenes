defmodule Run do
  def start_host(0) do
    System.cmd "bash", ["start.sh","0"]
  end
  def start_host(n) do
    Task.async(
      fn ->
        System.cmd "bash", ["start.sh",Integer.to_string(n)]
      end)
    start_host (n-1)
  end
end

{:ok, body} = File.read "config/config.exs"
lines_of_file = String.split body, "\n", trim: true
hosts_regex = ~r/^config :soe, number_of_hosts:[ ]*[0-9]*/
hosts_line = for line <- lines_of_file,
                 Regex.match?(hosts_regex, line) do
                    line
             end |> Enum.fetch!(0) |> String.trim
no_of_hosts = String.split(hosts_line, " ") |> List.last |> String.to_integer
IO.inspect "Starting " <> Integer.to_string(no_of_hosts) <> " nodes..."
Run.start_host no_of_hosts-1
