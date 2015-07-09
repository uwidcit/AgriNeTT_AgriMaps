# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0011_auto_20150211_1413'),
    ]

    operations = [
        migrations.AlterField(
            model_name='soilfeatures',
            name='exch_cations',
            field=models.CharField(default=None, max_length=30),
            preserve_default=True,
        ),
    ]
