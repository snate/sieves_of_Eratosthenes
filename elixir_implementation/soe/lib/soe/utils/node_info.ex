defmodule Soe.Utils.NodeInfo do

  @doc """
  Returns the hash of the node
  """
  def hash
  when node == :nonode@nohost do
    null_value
  end

  def hash do
    ~r/SoE(?<number>[0-9]+)@/
    |> Regex.named_captures(Atom.to_string(node()))
    |> map_with_zero
    |> Map.get("number")
    |> String.to_integer
  end

  defp map_with_zero(nil) do
    %{}
    |> Map.put_new("number", null_value)
  end

  defp map_with_zero(map) do
    map
  end

  defp null_value do
    -1
  end

  @doc """
  Returns the hash of the node
  """
  def compose_node_name(num)
  when is_integer(num) do
    compose_node_name Integer.to_string(num)
  end

  def compose_node_name(num) do
    "SoE" <> num <> "@127.0.0.1"
  end

  def endpoint_address do
    {:Server, :"endpoint@127.0.0.1"}
  end
end
