defmodule SoeClient.PrimesList do
  use GenServer

  @doc """
  Starts the primes' list.
  """
  def start_link do
    GenServer.start_link(__MODULE__, [], name: __MODULE__)
  end

  @doc """
  Get the primes' list.
  """
  def get do
    GenServer.call __MODULE__, {:get, []}
  end

  @doc """
  Append a number.
  """
  def append(num) do
    GenServer.call __MODULE__, {:append, num}
  end

  # SERVER CALLBACKS
  def handle_call({:get, []}, _from, primes) do
    {:reply, primes, []}
  end

  def handle_call({:append, new_prime}, _from, primes) do
    {:reply, :ok, [ new_prime | primes ] }
  end
end
