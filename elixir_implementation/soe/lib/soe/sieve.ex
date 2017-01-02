defmodule Soe.Sieve do
  use GenServer

  @doc """
  Starts the sieve.
  """
  def start_link(id, num) do
    name = String.to_atom("Sieve" <> Integer.to_string(id))
    IO.inspect "Creating sieve #{id}, #{num}"
    GenServer.call(endpoint, {:answer_for, {num, :prime}})
    GenServer.start_link(__MODULE__, [id, num], name: name)
  end

  # SERVER CALLBACKS
  def handle_cast({:is_prime?, param}, state)
  when (not is_integer(param)) do
    {:noreply, state}
  end

  def handle_cast({:is_prime?, num}, [id, prime])
  when num == prime do
    GenServer.call(endpoint, {:answer_for, {num, :prime}})
    {:noreply, [id, prime]}
  end

  def handle_cast({:is_prime?, number}, [id, prime])
  when rem(number,prime) != 0 do
    no_hosts = Application.get_env(:soe,:number_of_hosts)
    next_node = Soe.Utils.NodeInfo.hash + 1
    |> rem(no_hosts)
    |> Soe.Utils.NodeInfo.compose_node_name
    |> String.to_atom
    IO.inspect "{#{soe_receiver}, #{next_node}} and number is #{number}"
    GenServer.call({soe_receiver, next_node}, {:next_number, [id, number]})
    {:noreply, [id, prime]}
  end

  def handle_cast({:is_prime?, num}, state) do
    GenServer.call endpoint, {:answer_for, {num, :not_prime}}
    {:noreply, state}
  end

  defp soe_receiver do
    String.to_atom "Receiver"
  end

  defp endpoint do
    Soe.Utils.NodeInfo.endpoint_address
  end
end
