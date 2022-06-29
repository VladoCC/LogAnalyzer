package farpost.task;

import farpost.task.exceptions.UnexpectedArgumentException;

public class Args {
    private float availability = -1;
    private float responseTime = -1;

    public Args(String[] args) {
        int pos = 0;
        while (pos < args.length) {
            String arg = args[pos];
            if (arg.equals("-u") && availability == -1) {
                availability = getArg(args, pos, "-u");
                if (availability > 100.0f || availability <= 0.0f) {
                    throw new UnexpectedArgumentException("value for -u must be in range (0, 100]");
                }
            } else if (arg.equals("-t") && responseTime == -1) {
                responseTime = getArg(args, pos, "-t");
                if (responseTime <= 0f) {
                    throw new UnexpectedArgumentException("value for -t must be positive");
                }
            } else {
                throw new UnexpectedArgumentException(arg);
            }
            pos += 2;
        }
        if (availability == -1 || responseTime == -1) {
            throw new UnexpectedArgumentException("not enough arguments");
        }
    }

    public Args(float availability, float responseTime) {
        this.availability = availability;
        this.responseTime = responseTime;
    }

    private float getArg(String[] args, int pos, String key) {
        if (args.length <= pos + 1) {
            throw new UnexpectedArgumentException("not enough arguments");
        }
        String value = args[pos + 1];
        if (pos + 1 >= args.length || value == null) {
            throw new UnexpectedArgumentException("expected value for argument " + key);
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            throw new UnexpectedArgumentException(value);
        }
    }

    public float getAvailability() {
        return availability;
    }

    public float getResponseTime() {
        return responseTime;
    }
}