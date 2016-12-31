defmodule Soe.Utils.Node_Info do

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
end
