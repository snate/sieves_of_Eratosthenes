defmodule SoeClient.Timer do
  use GenServer

  @doc """
  Starts the primes.
  """
  def start_link do
    GenServer.start_link(__MODULE__, 0, name: __MODULE__)
  end

  @doc """
  Starts the timer.
  """
  def set(time) do
    GenServer.call __MODULE__, {:set, time}
  end

  @doc """
  Stops the timer.
  """
  def lap(time) do
    GenServer.call __MODULE__, {:lap, time}
  end

  # SERVER CALLBACKS
  def handle_call({:set, time}, _from, _t) do
    {:reply, :timer_started, time}
  end

  # SERVER CALLBACKS
  def handle_call({:lap, end_time}, _from, start_time) do
    # I assume that both end_time and start_time are in us.
    lap_time = (end_time - start_time) / (1000 * 1000)
    IO.puts "It took #{lap_time} seconds."
    {:reply, :lap_printed, start_time}
  end

end
