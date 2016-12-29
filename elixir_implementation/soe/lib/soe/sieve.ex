defmodule Soe.Sieve do
  use GenServer

  @doc """
  Starts the sieve.
  """
  def start_link(id, num) do
    name = String.to_atom("Sieve" <> Integer.to_string(id))
    GenServer.start_link(__MODULE__, num, name: name)
  end

  # SERVER CALLBACKS
  def handle_call({:is_prime?, number}, _from, prime)
    when rem(number,prime) != 0 do
    {:reply, :possible_prime, prime}
  end

  def handle_call({:is_prime?, number}, _from, prime) do
    {:reply, :not_prime, prime}
  end
end
