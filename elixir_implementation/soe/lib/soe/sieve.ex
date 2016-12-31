defmodule Soe.Sieve do
  use GenServer

  @doc """
  Starts the sieve.
  """
  def start_link(id, num) do
    name = String.to_atom("Sieve" <> Integer.to_string(id))
    GenServer.start_link(__MODULE__, num, name: name)
  end

  def handle_cast({:is_prime?, param}, prime)
  when (not is_integer(param)) do
    {:noreply, prime}
  end

  # SERVER CALLBACKS
  def handle_cast({:is_prime?, number}, prime)
  when rem(number,prime) != 0 do
    IO.inspect Node.self
    # send to next sieve, if any
    {:noreply, prime}
  end

  # SERVER CALLBACKS
  def handle_cast({:is_prime?, _num}, prime) do
    {:noreply, prime}
  end
end
