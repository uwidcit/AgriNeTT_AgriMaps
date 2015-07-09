# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0008_rainfall'),
    ]

    operations = [
        migrations.RenameField(
            model_name='rainfall',
            old_name='geom',
            new_name='geomData',
        ),
    ]
